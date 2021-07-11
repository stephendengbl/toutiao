package com.heima.wemedia.dto;

import com.heima.common.dto.PageRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WmMaterialDto extends PageRequestDto {
    Integer isCollection; //1 查询收藏的
}
