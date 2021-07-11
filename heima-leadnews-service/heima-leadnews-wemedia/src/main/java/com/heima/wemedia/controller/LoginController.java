package com.heima.wemedia.controller;

import com.heima.common.dto.ResponseResult;
import com.heima.wemedia.dto.WmLoginDto;
import com.heima.wemedia.service.IWmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IWmUserService userService;

    @PostMapping("/in")
    public ResponseResult in(@RequestBody WmLoginDto dto){
        return userService.login(dto);
    }
}
