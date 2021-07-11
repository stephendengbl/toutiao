package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.common.aliyun.OSSService;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.common.dto.User;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.common.util.WeMediaThreadLocalUtil;
import com.heima.wemedia.dto.WmMaterialDto;
import com.heima.wemedia.entity.WmMaterial;
import com.heima.wemedia.entity.WmNewsMaterial;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.IWmMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.wemedia.service.IWmNewsMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * <p>
 * 自媒体图文素材信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-20
 */
@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements IWmMaterialService {

    // 注入OSS服务
    @Autowired
    private OSSService ossService;

    @Autowired
    private IWmNewsMaterialService newsMaterialService;

    @Override
    public ResponseResult upload(MultipartFile file) {
        // 需求
        // 1. 上传图片到阿里云OSS
        try {
            // 2. 获取图片的地址
            String url = ossService.upload(file);
            // 3. 保存素材表
            WmMaterial material = new WmMaterial();
            // 从本地线程获取当前用户
            User user = WeMediaThreadLocalUtil.get();
            if (user == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            }
            material.setUserId(user.getUserId());
            material.setUrl(url);
            material.setType(0);
            material.setIsCollection(false);
            material.setCreatedTime(new Date());
            this.save(material);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        // 返回成功的响应
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listByCollection(WmMaterialDto dto) {
        // 需求 根据收藏状态分页查询素材列表
        // 隐藏条件 用户只能查询自己的图片
        User user = WeMediaThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        IPage<WmMaterial> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmMaterial> query = new LambdaQueryWrapper<>();
        // 添加用户id查询
        query.eq(WmMaterial::getUserId, user.getUserId());
        // 根据收藏状态查询
        if (dto.getIsCollection() != null) {
            query.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }
        // 根据上传时间倒序排列
        query.orderByDesc(WmMaterial::getCreatedTime);
        IPage<WmMaterial> iPage = this.page(page, query);
        // 构建通用的分页响应
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(),
                iPage.getTotal(), iPage.getRecords());
        return result;
    }

    @Override
    public ResponseResult deleteById(Integer id) {
        // 需求
        // 需要判断用户是否登录
        User user = WeMediaThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 根据素材id查询素材
        WmMaterial wmMaterial = this.getById(id);
        if (wmMaterial == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 该素材是否已经被文章引用  文章-素材关系表中查询记录
        LambdaQueryWrapper<WmNewsMaterial> query = new LambdaQueryWrapper<>();
        query.eq(WmNewsMaterial::getMaterialId, id);
        int count = newsMaterialService.count(query);
        // 如果已经引用,提示不能删除
        if (count > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_CAN_NOT_DELETE);
        }
        // 否则可以删除
        // 从本地库删除图片
        this.removeById(id);
        // 调用阿里云OSS删除图片
        ossService.deleteFile(wmMaterial.getUrl());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateStatus(Integer id, int collection) {
        // 根据传入的状态进行更新
        // WmMaterial wmMaterial = this.getById(id);
        // wmMaterial.setIsCollection(collection == 1 ? true : false);
        // this.updateById(wmMaterial);

        // 需要用户登录
        User user = WeMediaThreadLocalUtil.get();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 根据条件更新
        LambdaUpdateWrapper<WmMaterial> update = new LambdaUpdateWrapper<>();
        // update wm_material set is_collection = #is_collection where id = #id
        // 指定查询条件
        update.eq(WmMaterial::getId, id);
        update.eq(WmMaterial::getUserId, user.getUserId());
        // 指定更新的列
        update.set(WmMaterial::getIsCollection, collection);
        this.update(update);
        return ResponseResult.okResult();
    }
}
