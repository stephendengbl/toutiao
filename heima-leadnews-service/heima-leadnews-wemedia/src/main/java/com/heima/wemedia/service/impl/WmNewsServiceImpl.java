package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.WeMediaThreadLocalUtil;
import com.heima.wemedia.dto.ContentDto;
import com.heima.wemedia.dto.NewsAuthDto;
import com.heima.wemedia.dto.WmNewsDto;
import com.heima.wemedia.dto.WmNewsPageDto;
import com.heima.wemedia.entity.WmMaterial;
import com.heima.wemedia.entity.WmNews;
import com.heima.wemedia.entity.WmNewsMaterial;
import com.heima.wemedia.entity.WmUser;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.IWmMaterialService;
import com.heima.wemedia.service.IWmNewsMaterialService;
import com.heima.wemedia.service.IWmNewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.wemedia.service.IWmUserService;
import com.heima.wemedia.vo.WmNewsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 自媒体图文内容信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-22
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements IWmNewsService {


    @Autowired
    private IWmMaterialService materialService;

    @Autowired
    private IWmNewsMaterialService wmNewsMaterialService;

    @Override
    public ResponseResult listByCondition(WmNewsPageDto dto) {
        // 根据多个条件分页查询文章列表
        LambdaQueryWrapper<WmNews> query = new LambdaQueryWrapper<>();
        // 查询文章状态
        if (dto.getStatus() != null) {
            query.eq(WmNews::getStatus, dto.getStatus());
        }
        // 根据关键字查询文章标题
        if (!StringUtils.isEmpty(dto.getKeyword())) {
            query.like(WmNews::getTitle, dto.getKeyword());
        }
        // 根据所属频道查询
        if (dto.getChannelId() != null) {
            query.eq(WmNews::getChannelId, dto.getChannelId());
        }
        // 根据发布时间查询
        if (dto.getBeginPubDate() != null) {
            query.gt(WmNews::getPublishTime, dto.getBeginPubDate());
        }
        if (dto.getEndPubDate() != null) {
            query.le(WmNews::getPublishTime, dto.getEndPubDate());
        }
        // 分页
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        // 根据发布时间降序
        query.orderByDesc(WmNews::getPublishTime);

        IPage<WmNews> iPage = this.page(page, query);
        // 构建通用的分页响应
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(),
                iPage.getTotal(), iPage.getRecords());
        return result;
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // 发送文章id的主题名称
    @Value("${topic.autoAuditTopic}")
    private String autoAuditTopic;


    // 指定上下架发送消息的主题名称
    @Value("${topic.upDownTopic}")
    private String upDownTopic;

    @Override
    public ResponseResult submit(WmNewsDto dto) {
        // 需求分析

        // 判断用户是否登录
        User user = WeMediaThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 判断是否存在id
        // 1 存在id,需要进行文章的修改
        if (dto.getId() != null) {
            // 删除文章id对应的关联表
            Wrapper<WmNewsMaterial> query = new LambdaQueryWrapper<>();
            ((LambdaQueryWrapper<WmNewsMaterial>) query).eq(WmNewsMaterial::getNewsId, dto.getId());
            wmNewsMaterialService.remove(query);
        }

        // 2 不存在id,需要进行文章的新增
        // 2.1 判断是保存草稿还是提交文章(待审核)

        // 提交文章 保存文章数据
        WmNews wmNews = new WmNews();
        // 使用dto中的属性来赋值
        BeanUtils.copyProperties(dto, wmNews);
        wmNews.setUserId(user.getUserId());
        wmNews.setCreatedTime(new Date());
        wmNews.setStatus(dto.getStatus());
        wmNews.setPublishTime(dto.getPublishTime());
        // 从前端传递的封面图片是集合,需要转换成逗号分隔的字符串
        String coverImage = getCoverFromImages(dto.getImages());
        wmNews.setImages(coverImage);// 封面图片,多张图片逗号分隔
        wmNews.setEnable(true);

        List<String> contentImages = getImagesFromContent(dto.getContent());
        // 如果封面类型是自动类型 需要从文章中提取图片作为封面
        if (dto.getType() == -1) {
            if (contentImages.size() <= 0) {
                // 内容中没有图片，则封面为无图
                wmNews.setType(0);
            } else if (contentImages.size() <= 2) {
                // 内容图片的个数小于等于2  则为单图,截取第一张图片为封面图片
                wmNews.setType(1);
                wmNews.setImages(contentImages.get(0));
            } else {
                // 内容图片大于等于3，则为多图，截取前三张图作为封面图片
                wmNews.setType(3);
                List<String> subList = contentImages.subList(0, 3);
                String fromImages = getCoverFromImages(subList);
                wmNews.setImages(fromImages);
            }
        }
        // 保存草稿,不需要记录提交时间 也不需要保存文章与图片的关联关系
        if (dto.getStatus() == 0) {
            // 保存草稿
            this.saveOrUpdate(wmNews);
            return ResponseResult.okResult();
        }
        wmNews.setSubmitedTime(new Date());
        this.saveOrUpdate(wmNews);
        // 如果有图片的引用,需要保存图片与文章的关联关系
        // 保存封面图片与文章的关联关系
        // 从wmNews中的图片转成集合
        if (!StringUtils.isEmpty(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            List<String> list = Arrays.asList(split);
            saveImageRelation(wmNews, list, 1);
            // 保存内容图片与文章的关联关系
            saveImageRelation(wmNews, contentImages, 0);
        }

        // 发送消息到kafka
        kafkaTemplate.send(autoAuditTopic, wmNews.getId().toString());

        // 返回结果
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteById(Integer id) {
        // 需求分析
        // 删除操作要求用户登录
        User user = WeMediaThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 根据id查询文章
        WmNews wmNews = this.getById(id);
        // 判断文章是否存在
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (!wmNews.getUserId().equals(user.getUserId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        // 文章存在判断文章当前的状态  如果是已上架且已发布 不允许删除
        if (wmNews.getStatus() == 9 && wmNews.getEnable()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_CAN_NOT_DELETE);
        }
        // 否则删除文章
        this.removeById(id);
        // 删除文章与图片的对应关系
        Wrapper<WmNewsMaterial> query = new LambdaQueryWrapper<>();
        ((LambdaQueryWrapper<WmNewsMaterial>) query).eq(WmNewsMaterial::getNewsId, id);
        wmNewsMaterialService.remove(query);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        // 需求
        // 删除操作要求用户登录
        User user = WeMediaThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 根据id查询文章
        WmNews wmNews = this.getById(dto.getId());
        // 判断文章是否存在
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 已发布的状态可以进行上下架操作
        if (wmNews.getStatus() != 9 || !wmNews.getUserId().equals(user.getUserId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        wmNews.setEnable(dto.getEnable() == 1 ? true : false);
        this.updateById(wmNews);
        //  上下架状态需要同步到文章库
        // 发送文章的id  当前上下架状态
        Map<String, Object> map = new HashMap<>();
        map.put("id", wmNews.getArticleId());
        map.put("isDown", !wmNews.getEnable());

        kafkaTemplate.send(upDownTopic, JSON.toJSONString(map));

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<List<Integer>> getRelease() {
        // 查询状态为4 或者8 且发布时间小于等于当前时间
        LambdaQueryWrapper<WmNews> query = new LambdaQueryWrapper<>();
        query.in(WmNews::getStatus, 4, 8);
        query.le(WmNews::getPublishTime, new Date());
        query.select(WmNews::getId);
        List<Integer> ids = this.listObjs(query, x -> (Integer) x);
        return ResponseResult.okResult(ids);
    }

    @Autowired
    private IWmUserService userService;

    @Override
    public PageResponseResult findPageByName(NewsAuthDto dto) {
        // 需求分析
        // 1 根据标题模糊查询
        // 2 根据状态查询
        // 3. 分页查询
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());

        LambdaQueryWrapper<WmNews> query = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(dto.getTitle())) {
            query.like(WmNews::getTitle, dto.getTitle());
        }
        if (dto.getStatus() != null) {
            query.eq(WmNews::getStatus, dto.getStatus());
        }
        // 根据发布时间倒序排
        query.orderByDesc(WmNews::getPublishTime);
        IPage<WmNews> iPage = this.page(page, query);
        List<WmNews> newsList = iPage.getRecords();
        // 4. 构建vo对象
        List<WmNewsVo> vos = new ArrayList<>();
        // 将所有的用户id列表拿出来,一次性在wmUser表中做查询
        for (WmNews wmNews : newsList) {
            WmNewsVo vo = new WmNewsVo();
            BeanUtils.copyProperties(wmNews, vo);
            // 查询作者的名称
            WmUser wmUser = userService.getById(wmNews.getUserId());
            vo.setAuthorName(wmUser.getName());
            vos.add(vo);
        }
        // 5. 返回vo列表
        return new PageResponseResult(dto.getPage(), dto.getSize(), iPage.getTotal(), vos);
    }

    @Override
    public ResponseResult<WmNewsVo> findNewsVoById(Integer id) {

        // 1. 根据id查询自媒体文章
        WmNews wmNews = this.getById(id);
        // 2. 查询作者信息
        WmUser wmUser = userService.getById(wmNews.getUserId());
        // 3. 封装vo
        WmNewsVo vo = new WmNewsVo();
        BeanUtils.copyProperties(wmNews, vo);
        vo.setAuthorName(wmUser.getName());
        return ResponseResult.okResult(vo);
    }

    /**
     * 从文章内容中提取图片
     *
     * @param content
     * @return
     */
    private List<String> getImagesFromContent(String content) {
        List<String> list = new ArrayList<>();
        if (!StringUtils.isEmpty(content)) {
            // 将内容json转换成对象集合
            List<ContentDto> contentDtos = JSON.parseArray(content, ContentDto.class);
            for (ContentDto contentDto : contentDtos) {
                if (contentDto.getType().equals("image")) {
                    list.add(contentDto.getValue());
                }
            }
        }
        return list;
    }

    /**
     * 保存图片与文章的关系
     *
     * @param wmNews 文章
     * @param images 图片集合
     * @param type   类型 0:内容  1:封面
     */
    private void saveImageRelation(WmNews wmNews, List<String> images, int type) {
        int ord = 0;
        for (String image : images) {
            // 获取素材 根据图片url查询
            Wrapper<WmMaterial> query = new LambdaQueryWrapper<>();
            ((LambdaQueryWrapper<WmMaterial>) query).eq(WmMaterial::getUrl, image);
            WmMaterial wmMaterial = materialService.getOne(query);
            if (wmMaterial != null) {
                WmNewsMaterial wmNewsMaterial = new WmNewsMaterial();
                wmNewsMaterial.setMaterialId(wmMaterial.getId());
                wmNewsMaterial.setNewsId(wmNews.getId());
                wmNewsMaterial.setType(type);
                wmNewsMaterial.setOrd(ord);
                wmNewsMaterialService.save(wmNewsMaterial);
                ord++;
            }
        }
    }

    /**
     * 封面图片是集合,需要转换成逗号分隔的字符串
     *
     * @param images
     * @return
     */
    private String getCoverFromImages(List<String> images) {
        if (images.size() > 0) {
            String image = String.join(",", images);
            return image;
        }
        return null;
    }
}
