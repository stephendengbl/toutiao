package com.heima.admin.controller;

import com.heima.admin.dto.NewsAuthDto;
import com.heima.admin.service.IAuthService;
import com.heima.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news_auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody NewsAuthDto dto) {
        return authService.list(dto);
    }

    @GetMapping("/one/{id}")
    public ResponseResult one(@PathVariable("id") Integer id) {
        return authService.one(id);
    }

    /**
     * 审核通过
     *
     * @param dto
     * @return
     */
    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDto dto) {
        return authService.auth(dto);
    }

    /**
     * 审核通过
     *
     * @param dto
     * @return
     */
    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDto dto) {
        return authService.auth(dto);
    }
}
