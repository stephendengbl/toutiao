package com.heima.wemedia.service;

import com.heima.common.dto.ResponseResult;
import com.heima.wemedia.dto.WmMaterialDto;
import com.heima.wemedia.entity.WmMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 自媒体图文素材信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-20
 */
public interface IWmMaterialService extends IService<WmMaterial> {

    ResponseResult upload(MultipartFile file);

    ResponseResult listByCollection(WmMaterialDto dto);

    ResponseResult deleteById(Integer id);

    ResponseResult updateStatus(Integer id, int collection);
}
