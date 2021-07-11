package com.heima.admin.controller;

import com.heima.admin.service.IAuditService;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private GreenTextScan textScan;

    @Autowired
    private GreenImageScan imageScan;

    @Autowired
    private IAuditService auditService;

    @GetMapping("/{id}")
    public String testAuditById(@PathVariable("id") Integer id) {
        auditService.auditById(id);
        return "success";
    }

    @PostMapping("/text")
    public String testContentScan(String content) throws Exception {
        Map map = textScan.greenTextScan(content);
        return map.get("suggestion").toString();
    }


    @PostMapping("/image")
    public String testImageScan(String url) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(url);
        Map map = imageScan.checkUrl(list);
        return map.get("suggestion").toString();
    }


}
