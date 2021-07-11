package com.heima.article.job;

import com.heima.article.service.IHotArticleService;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HotArticleComputeJob {

    @Autowired
    private IHotArticleService hotArticleService;

    @XxlJob("computeHotArticleJob")
    public ReturnT<String> auditJobHandler(String param) throws Exception {
        // 计算前五天文章分值
        try {
            System.out.println("计算文章分值任务开始...");
            hotArticleService.compute();
            System.out.println("计算文章分值任务完成");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ReturnT.FAIL;
        }

    }
}
