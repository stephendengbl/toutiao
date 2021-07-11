package com.heima.user.dto;

import com.heima.common.dto.PageRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthDto extends PageRequestDto {

    /**
     * 用户认证表ID
     */
    private Integer id;

    /**
     * 驳回原因
     */
    private String reason;

    /**
     * 状态
     */
    private Integer status;
}
