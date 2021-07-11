package com.heima.user.service;

        import com.heima.common.dto.ResponseResult;
        import com.heima.user.dto.AuthDto;
        import com.heima.user.entity.ApUserRealname;
        import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * APP实名认证信息表 服务类
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
public interface IApUserRealnameService extends IService<ApUserRealname> {

    ResponseResult listByStatus(AuthDto dto);

    ResponseResult auth(AuthDto dto, int type);
}
