package com.heima.admin.controller;

import com.heima.admin.dto.AdUserDto;
import com.heima.admin.service.IAdUserService;
import com.heima.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IAdUserService userService;

    @PostMapping("/in")
    public ResponseResult in(@RequestBody AdUserDto dto){
        return userService.login(dto);
    }
}
