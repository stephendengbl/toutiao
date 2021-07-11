package com.heima.admin.dto;

import com.heima.common.dto.PageRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewsAuthDto extends PageRequestDto {
    /**
     * 标题
     */
    private String title;
    /**
     * 状态
     */
    private Integer status;

    /**
     * id
     */
    private Integer id;

    /**
     * 失败原因
     */
    private String msg;
}
