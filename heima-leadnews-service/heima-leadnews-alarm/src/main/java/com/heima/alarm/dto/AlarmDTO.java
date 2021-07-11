package com.heima.alarm.dto;

import lombok.Data;

/**
 * Created by 传智播客*黑马程序员.
 */
@Data
public class AlarmDTO {
    private Integer scopeId;
    private String scope;
    private String name;
    private String id0;
    private String id1;
    private String ruleName;
    private String alarmMessage;
    private Long startTime;

}
