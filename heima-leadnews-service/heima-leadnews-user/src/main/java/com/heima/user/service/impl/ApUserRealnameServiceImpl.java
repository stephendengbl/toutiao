package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import com.heima.user.dto.ApAuthor;
import com.heima.user.dto.AuthDto;
import com.heima.user.dto.WmUser;
import com.heima.user.entity.ApUser;
import com.heima.user.entity.ApUserRealname;
import com.heima.user.feign.ArticleFeign;
import com.heima.user.feign.WeMediaFeign;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.IApUserRealnameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.user.service.IApUserService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * APP实名认证信息表 服务实现类
 * </p>
 *
 * @author mcm
 * @since 2021-05-19
 */
@Service
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements IApUserRealnameService {


    @Autowired
    private IApUserService userService;

    @Autowired
    private WeMediaFeign weMediaFeign;

    @Autowired
    private ArticleFeign articleFeign;

    @Override
    public ResponseResult listByStatus(AuthDto dto) {
        // 需求  根据状态分页查询用户认证列表

        // 构建分页条件
        IPage<ApUserRealname> page = new Page<>(dto.getPage(), dto.getSize());
        // 构建查询条件
        LambdaQueryWrapper<ApUserRealname> query = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null) {
            query.eq(ApUserRealname::getStatus, dto.getStatus());
        }
        // 执行查询
        IPage<ApUserRealname> iPage = this.page(page, query);
        // 构建分页响应
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(),
                iPage.getTotal(), iPage.getRecords());
        return result;
    }

    /**
     * 审核用户认证
     *
     * @param dto
     * @param type 1 为成功  0 为失败
     * @return
     */
    @Override
    // @Transactional
    @GlobalTransactional
    public ResponseResult auth(AuthDto dto, int type) {
        // 需求分析

        // 根据id查询用户认证信息
        ApUserRealname apUserRealname = this.getById(dto.getId());
        if (apUserRealname == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 判断类型是成功还是失败
        if (type == 0) {
            // 如果是失败 更新用户认证表的状态为 2 并且记录失败原因
            apUserRealname.setStatus(2); // 在企业中状态值比较多的情况下,一般定义为枚举
            apUserRealname.setReason(dto.getReason());
            // 保存用户认证信息
            this.updateById(apUserRealname);
            return ResponseResult.okResult();
        }

        // 如果是成功 更新用户认证表的状态为9
        apUserRealname.setStatus(9);
        this.updateById(apUserRealname);

        // 查询ap_user信息
        ApUser apUser = userService.getById(apUserRealname.getUserId());


        // 创建自媒体用户
        WmUser wmUser = createWmUser(apUser);
        if (wmUser != null) {
            // 创建文章作者  -- 更新自媒体用户中的作者编号
            ApAuthor author = createAuthor(apUser, wmUser);
            if (author != null) {
                // 模拟异常
                // int i = 1 / 0;
                // 更新自媒体用户中的作者编号
                wmUser.setApAuthorId(author.getId());
                weMediaFeign.updateWmUser(wmUser);
                // 更新ap_user表的表示flag
                apUser.setFlag(1);
                userService.updateById(apUser);
            }
        }
        return ResponseResult.okResult();
    }

    /**
     * 创建作者
     *
     * @param apUser
     * @param wmUser
     * @return
     */
    private ApAuthor createAuthor(ApUser apUser, WmUser wmUser) {

        // 远程调用
        ApAuthor author = new ApAuthor();
        author.setName(apUser.getName());
        author.setType(2);
        author.setUserId(apUser.getId());
        author.setWmUserId(wmUser.getId());
        author.setCreatedTime(new Date());
        ResponseResult<ApAuthor> apAuthorResponseResult = articleFeign.saveAuthor(author);
        if (apAuthorResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            ApAuthor apAuthor = apAuthorResponseResult.getData();
            return apAuthor;
        }
        return null;
    }

    /**
     * 创建自媒体用户
     *
     * @param apUser
     * @return
     */
    private WmUser createWmUser(ApUser apUser) {
        // 调用自媒体远程接口
        WmUser wmUser = new WmUser();
        // 属性赋值 参数 1 源  2 目标
        BeanUtils.copyProperties(apUser, wmUser);
        wmUser.setApUserId(apUser.getId());
        wmUser.setNickname(apUser.getName());
        wmUser.setStatus(9);
        wmUser.setType(0);
        wmUser.setCreatedTime(new Date());
        ResponseResult<WmUser> wmUserResponseResult = weMediaFeign.saveWmUser(wmUser);
        if (wmUserResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            // 获取数据
            WmUser wmUser1 = wmUserResponseResult.getData();
            return wmUser1;
        }
        return null;
    }
}
