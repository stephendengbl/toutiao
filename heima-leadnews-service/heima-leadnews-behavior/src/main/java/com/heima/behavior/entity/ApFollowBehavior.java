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
 * APP关注行为表
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ap_follow_behavior")
@ApiModel(description="APP关注行为表")
public class ApFollowBehavior implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实体ID
     */
    @ApiModelProperty(value = "实体ID")
    @TableField("entry_id")
    private Integer entryId;

    /**
     * 关注用户ID
     */
    @ApiModelProperty(value = "关注用户ID")
    @TableField("follow_id")
    private Integer followId;

    /**
     * 操作类型: 0 关注 1 取消关注
     */
    @ApiModelProperty(value = "操作类型: 0 关注 1 取消关注")
    @TableField("operation")
    private Integer operation;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间")
    @TableField("created_time")
    private Date createdTime;


}
