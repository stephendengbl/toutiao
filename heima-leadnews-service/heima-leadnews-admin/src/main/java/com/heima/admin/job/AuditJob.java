package com.heima.admin.job;

import com.heima.admin.feign.WeMediaFeign;
import com.heima.admin.service.IAuditService;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AuditJob {

    @Autowired
    private WeMediaFeign weMediaFeign;

    @Autowired
    private IAuditService auditService;

    @XxlJob("autoAuditJob")
    public ReturnT<String> auditJobHandler(String param) throws Exception {
        // 查询待发布的文章id列表
        try {
            ResponseResult<List<Integer>> responseResult = weMediaFeign.getRelease();
            if (responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
                List<Integer> ids = responseResult.getData();
                for (Integer id : ids) {
                    // 调用审核服务
                    auditService.auditById(id);
                }
            }
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ReturnT.FAIL;
        }

    }
}
