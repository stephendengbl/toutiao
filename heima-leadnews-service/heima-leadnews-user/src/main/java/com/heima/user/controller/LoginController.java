package com.heima.user.controller;

import com.heima.common.dto.ResponseResult;
import com.heima.user.dto.LoginDto;
import com.heima.user.service.IApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    @Autowired
    private IApUserService userService;

    /**
     * App端登录
     *
     * @param dto
     * @return
     */
    @PostMapping("/login_auth")
    public ResponseResult in(@RequestBody LoginDto dto) {
        return userService.login(dto);
    }
}
