package com.heima.wemedia.vo;

import com.heima.wemedia.entity.WmNews;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WmNewsVo  extends WmNews {
    /**
     * 作者名称
     */
    private String authorName;
}
