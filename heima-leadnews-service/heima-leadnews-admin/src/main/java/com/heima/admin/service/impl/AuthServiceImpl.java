package com.heima.admin.service.impl;

import com.heima.admin.dto.NewsAuthDto;
import com.heima.admin.dto.WmNews;
import com.heima.admin.feign.WeMediaFeign;
import com.heima.admin.service.IAuthService;
import com.heima.common.dto.PageResponseResult;
import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private WeMediaFeign weMediaFeign;

    @Override
    public ResponseResult list(NewsAuthDto dto) {
        // 调用自媒体的远程接口
        PageResponseResult result = weMediaFeign.findPageByName(dto);
        return result;
    }

    @Override
    public ResponseResult one(Integer id) {
        return weMediaFeign.findNewsVoById(id);
    }

    @Override
    public ResponseResult auth(NewsAuthDto dto) {
        // 需求分析
        ResponseResult<WmNews> wmNewsResponseResult = weMediaFeign.getById(dto.getId());
        if (wmNewsResponseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            WmNews wmNews = wmNewsResponseResult.getData();
            // 1. 判断状态  状态为2 审核失败 需要记录审核失败的原因 更新自媒体文章
            // 2. 状态为4 人工审核通过 更新自媒体文章
            if (dto.getStatus() == 4) {
                wmNews.setStatus(4);
            }
            if (dto.getStatus() == 2) {
                wmNews.setStatus(2);
                wmNews.setReason(dto.getMsg());
            }
            weMediaFeign.updateWmNews(wmNews);
        }
        return ResponseResult.okResult();
    }
}
