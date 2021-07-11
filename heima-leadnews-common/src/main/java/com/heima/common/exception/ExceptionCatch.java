package com.heima.common.exception;

import com.heima.common.dto.ResponseResult;
import com.heima.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 针对于RestController添加了控制器切面
@Slf4j
public class ExceptionCatch {

    // 指定要捕获什么类型的异常
    @ExceptionHandler(value = Exception.class)
    public ResponseResult handleMessage(Exception ex) {
        log.error(ex.getMessage());
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }
}
