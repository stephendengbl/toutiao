package com.heima.behavior.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * APP阅读行为表
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ap_read_behavior")
@ApiModel(description="APP阅读行为表")
public class ApReadBehavior implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("entry_id")
    private Integer entryId;

    /**
     * 文章ID
     */
    @ApiModelProperty(value = "文章ID")
    @TableField("article_id")
    private Long articleId;

    /**
     * 阅读次数
     */
    @ApiModelProperty(value = "阅读次数")
    @TableField("count")
    private Integer count;

    /**
     * 阅读时间单位秒
     */
    @ApiModelProperty(value = "阅读时间单位秒")
    @TableField("read_duration")
    private Integer readDuration;

    /**
     * 阅读百分比
     */
    @ApiModelProperty(value = "阅读百分比")
    @TableField("percentage")
    private Integer percentage;

    /**
     * 文章加载时间
     */
    @ApiModelProperty(value = "文章加载时间")
    @TableField("load_duration")
    private Integer loadDuration;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间")
    @TableField("created_time")
    private Date createdTime;

    @TableField("updated_time")
    private Date updatedTime;


}
