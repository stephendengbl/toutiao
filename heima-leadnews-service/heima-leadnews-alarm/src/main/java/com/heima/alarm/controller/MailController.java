package com.heima.alarm.controller;

import com.heima.alarm.config.AlarmEmailProperties;
import com.heima.alarm.dto.AlarmDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alarm")
@Slf4j
public class MailController {

    @Autowired
    private AlarmEmailProperties alarmEmailProperties;

    @Autowired
    private JavaMailSender javaMailSender;

    @PostMapping("/mailNotify")
    public void emailAlarm(@RequestBody List<AlarmDTO> alarmDTOList) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //从哪个邮箱发出
        mailMessage.setFrom(alarmEmailProperties.getFrom());
        //发送邮件
        mailMessage.setTo(alarmEmailProperties.getReceiveEmails().toArray(new String[]{}));
        //主题
        mailMessage.setSubject("skywalking告警邮件");
        //邮件内容
        mailMessage.setText(alarmDTOList.toString());
        javaMailSender.send(mailMessage);
        log.info("告警邮件已发送");
    }
}
