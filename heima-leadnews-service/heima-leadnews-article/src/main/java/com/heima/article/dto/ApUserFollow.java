package com.heima.article.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * APP用户关注信息表
 * </p>
 *
 * @author mcm
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ap_user_follow")
@ApiModel(description="APP用户关注信息表")
public class ApUserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Integer userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @TableField("user_name")
    private String userName;

    /**
     * 关注作者ID
     */
    @ApiModelProperty(value = "关注作者ID")
    @TableField("follow_id")
    private Integer followId;

    /**
     * 粉丝昵称
     */
    @ApiModelProperty(value = "粉丝昵称")
    @TableField("follow_name")
    private String followName;

    /**
     * 关注度
            0 偶尔感兴趣
            1 一般
            2 经常
            3 高度
     */
    @ApiModelProperty(value = "关注度            0 偶尔感兴趣            1 一般            2 经常            3 高度")
    @TableField("level")
    private Integer level;

    /**
     * 是否动态通知
     */
    @ApiModelProperty(value = "是否动态通知")
    @TableField("is_notice")
    private Boolean isNotice;

    /**
     * 是否屏蔽评论
     */
    @ApiModelProperty(value = "是否屏蔽评论")
    @TableField("is_shield_comment")
    private Boolean isShieldComment;

    /**
     * 是否可见我动态
     */
    @ApiModelProperty(value = "是否可见我动态")
    @TableField("is_display")
    private Boolean isDisplay;

    /**
     * 是否屏蔽私信
     */
    @ApiModelProperty(value = "是否屏蔽私信")
    @TableField("is_shield_letter")
    private Boolean isShieldLetter;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("created_time")
    private Date createdTime;


}
