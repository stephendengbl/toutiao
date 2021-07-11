package com.heima.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.admin.dto.ArticleDto;
import com.heima.admin.dto.ContentDto;
import com.heima.admin.dto.WmNews;
import com.heima.admin.dto.WmUser;
import com.heima.admin.entity.AdChannel;
import com.heima.admin.entity.AdSensitive;
import com.heima.admin.entity.ApArticleSearch;
import com.heima.admin.feign.ArticleFeign;
import com.heima.admin.feign.WeMediaFeign;
import com.heima.admin.repository.ArticleRepository;
import com.heima.admin.service.IAdChannelService;
import com.heima.admin.service.IAdSensitiveService;
import com.heima.admin.service.IAuditService;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.SensitiveWordUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class AuditServiceImpl implements IAuditService {

    @Autowired
    private WeMediaFeign weMediaFeign;

    @Autowired
    private ArticleFeign articleFeign;

    @Autowired
    private GreenTextScan textScan;

    @Autowired
    private GreenImageScan imageScan;

    @Override
    @GlobalTransactional
    public void auditById(Integer id) {
        // 1. 根据id查询自媒体文章
        // 1.1 远程调用自媒体接口
        ResponseResult<WmNews> wmNewsResponseResult = weMediaFeign.getById(id);
        if (!wmNewsResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode()) ||
                wmNewsResponseResult.getData() == null) {
            return;
        }
        WmNews wmNews = wmNewsResponseResult.getData();
        // 2. 判断自媒体文章的状态和发布时间
        // 状态 0 草稿 1 提交（待审核）  2 审核失败  3 人工审核 4 人工审核通过  8 审核通过（待发布）  9 已发布
        // 状态为4 判断发布时间是否大于当前时间 是-->发布文章  否-->不用处理
        // 状态为8 判断发布时间是否大于当前时间 是-->发布文章  否-->不用处理
        if (wmNews.getStatus() == 4 || wmNews.getStatus() == 8) {
            // 判断发布时间是否大于当前时间
            if (wmNews.getPublishTime().getTime() < System.currentTimeMillis()) {
                saveArticle(wmNews);
            }
        }

        if (wmNews.getStatus() == 1) {
            // 3. 状态为1 需要自动审核
            // 3.1 敏感词审核
            boolean resultSensitive = checkBySensitive(wmNews);
            // 3.2 阿里云文本审核
            if (!resultSensitive) {
                return;
            }
            boolean textAliyun = checkText(wmNews);
            if (!textAliyun) {
                return;
            }
            // 3.3 阿里云图片审核
            boolean imageAliyun = checkImage(wmNews);
            if (!imageAliyun) return;

            // 3.4 判断发布时间 如果不大于当前时间 直接发布文章 否则修改文章状态为8
            if (wmNews.getPublishTime().getTime() >= System.currentTimeMillis()) {
                // 修改文章状态为8
                wmNews.setStatus(8);
                weMediaFeign.updateWmNews(wmNews);
            } else {
                saveArticle(wmNews);
            }
        }


    }

    @Autowired
    private IAdChannelService channelService;

    /**
     * 保存文章
     *
     * @param wmNews
     */
    private void saveArticle(WmNews wmNews) {
        // 调用文章服务的远程接口
        ArticleDto dto = new ArticleDto();
        // 属性赋值
        BeanUtils.copyProperties(wmNews, dto);
        // 查询作者的id和名称
        ResponseResult<WmUser> userResponseResult = weMediaFeign.getUserById(wmNews.getUserId());
        if (userResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            WmUser wmUser = userResponseResult.getData();
            dto.setAuthorId(wmUser.getApAuthorId());
            dto.setAuthorName(wmUser.getName());
        }
        // 查询channelName
        AdChannel adChannel = channelService.getById(wmNews.getChannelId());
        if (adChannel != null) {
            dto.setChannelName(adChannel.getName());
        }
        dto.setLayout(wmNews.getType());
        dto.setFlag(0);
        dto.setCreatedTime(new Date());
        // 设置内容
        dto.setContent(wmNews.getContent());
        ResponseResult<Long> saveArticle = articleFeign.saveArticle(dto);
        if (saveArticle.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            Long articleId = saveArticle.getData();
            // 模拟异常
            // int i = 1 / 0;
            // 更新文章id到自媒体文章中
            wmNews.setStatus(9);
            wmNews.setArticleId(articleId);
            weMediaFeign.updateWmNews(wmNews);

            // 创建ES索引
            ApArticleSearch apArticleSearch = new ApArticleSearch();
            apArticleSearch.setId(articleId);
            apArticleSearch.setTitle(wmNews.getTitle());
            apArticleSearch.setLayout(wmNews.getType());
            apArticleSearch.setImages(wmNews.getImages());
            apArticleSearch.setPublishTime(wmNews.getPublishTime());
            articleRepository.save(apArticleSearch);
        }
    }

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * 图片审核
     *
     * @param wmNews
     * @return
     */
    private boolean checkImage(WmNews wmNews) {
        Map<String, Object> textAndImageFromContent = getTextAndImageFromContent(wmNews.getContent());
        List<String> images = (List<String>) textAndImageFromContent.get("images");
        if (images.size() > 0) {
            try {
                Map map = imageScan.checkUrl(images);
                // 获取到阿里云的结果  分析结果
                String suggestion = (String) map.get("suggestion");
                // pass  通过   block 拒绝  review 需要人工审核
                // 针对于这三种结果做不同的操作
                // 通过 返回true
                if (suggestion.equals("pass")) {
                    return true;
                }
                String label = (String) map.get("label");
                // block 需要更新自媒体文章状态为 2 审核失败 保存 返回false
                if (suggestion.equals("block")) {
                    wmNews.setStatus(2);
                    wmNews.setReason("阿里云文本审核失败,标签为: " + label);
                    weMediaFeign.updateWmNews(wmNews);
                    return false;
                }
                // review 需要更新自媒体文章状态为 3 人工审核 保存 返回false
                if (suggestion.equals("review")) {
                    wmNews.setStatus(3);
                    wmNews.setReason("阿里云文本审核需要人工审核,标签为: " + label);
                    weMediaFeign.updateWmNews(wmNews);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 阿里云文本审核
     *
     * @param wmNews
     * @return
     */
    private boolean checkText(WmNews wmNews) {
        // 审核文章的标题和内容  将标题和内容拼接后调用阿里云接口
        Map<String, Object> textAndImageFromContent = getTextAndImageFromContent(wmNews.getContent());
        String content = wmNews.getTitle() + textAndImageFromContent.get("text");
        try {
            Map map = textScan.greenTextScan(content);
            // 获取到阿里云的结果  分析结果
            String suggestion = (String) map.get("suggestion");
            // pass  通过   block 拒绝  review 需要人工审核
            // 针对于这三种结果做不同的操作
            // 通过 返回true
            if (suggestion.equals("pass")) {
                return true;
            }
            String label = (String) map.get("label");
            // block 需要更新自媒体文章状态为 2 审核失败 保存 返回false
            if (suggestion.equals("block")) {
                wmNews.setStatus(2);
                wmNews.setReason("阿里云文本审核失败,标签为: " + label);
                weMediaFeign.updateWmNews(wmNews);
                return false;
            }
            // review 需要更新自媒体文章状态为 3 人工审核 保存 返回false
            if (suggestion.equals("review")) {
                wmNews.setStatus(3);
                wmNews.setReason("阿里云文本审核需要人工审核,标签为: " + label);
                weMediaFeign.updateWmNews(wmNews);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Autowired
    private IAdSensitiveService sensitiveService;

    /**
     * 敏感词审核
     *
     * @param wmNews
     * @return
     */
    private boolean checkBySensitive(WmNews wmNews) {
        // 构建DFA的Map
        if (SensitiveWordUtil.dictionaryMap.size() <= 0) {
            // 从敏感词表中查询敏感词
            LambdaQueryWrapper<AdSensitive> query = new LambdaQueryWrapper<>();
            // 指定需要返回的字段 相当于 select sensitives from ad_sensitive
            query.select(AdSensitive::getSensitives);
            List<String> list = sensitiveService.listObjs(query, x -> (String) x);
            // 初始化map
            SensitiveWordUtil.initMap(list);
        }
        // 判断文章的标题和内容是否合法
        Map<String, Integer> map = SensitiveWordUtil.matchWords(wmNews.getTitle());

        if (map.size() > 0) {
            // 跟新自媒体文章状态,记录拒绝原因
            wmNews.setStatus(2);
            wmNews.setReason("敏感词校验失败");
            weMediaFeign.updateWmNews(wmNews);
            return false;
        }

        // 获取内容
        Map<String, Object> textAndImageFromContent = getTextAndImageFromContent(wmNews.getContent());
        String text = (String) textAndImageFromContent.get("text");
        if (!StringUtils.isEmpty(text)) {
            Map<String, Integer> contentResult = SensitiveWordUtil.matchWords(text);
            if (contentResult.size() > 0) {
                wmNews.setStatus(2);
                wmNews.setReason("敏感词校验失败");
                weMediaFeign.updateWmNews(wmNews);
                return false;
            }
        }
        return true;
    }


    /**
     * 提取内容中的文本和图片
     *
     * @param content
     * @return
     */
    private Map<String, Object> getTextAndImageFromContent(String content) {
        Map<String, Object> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        List<String> images = new ArrayList<>();
        // 分析内容,转换成对象集合
        List<ContentDto> contentDtos = JSON.parseArray(content, ContentDto.class);
        for (ContentDto contentDto : contentDtos) {
            if (contentDto.getType().equals("text")) {
                // 文本内容
                sb.append(contentDto.getValue());
            }
            if (contentDto.getType().equals("image")) {
                // 图片内容
                images.add(contentDto.getValue());
            }
        }
        map.put("text", sb.toString());
        map.put("images", images);
        return map;
    }
}
