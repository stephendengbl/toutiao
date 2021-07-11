package com.heima.admin.dto;

import com.heima.common.dto.PageRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SensitiveDto extends PageRequestDto {
    /**
     * 关键字
     */
    private String name;
}
