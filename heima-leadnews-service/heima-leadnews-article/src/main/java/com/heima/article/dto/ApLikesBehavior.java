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
 * APP点赞行为表
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ap_likes_behavior")
@ApiModel(description="APP点赞行为表")
public class ApLikesBehavior implements Serializable {

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
     * 文章ID
     */
    @ApiModelProperty(value = "文章ID")
    @TableField("article_id")
    private Long articleId;

    /**
     * 点赞内容类型
            0文章
            1动态
     */
    @ApiModelProperty(value = "点赞内容类型            0文章            1动态")
    @TableField("type")
    private Integer type;

    /**
     * 0 点赞
            1 取消点赞
     */
    @ApiModelProperty(value = "0 点赞            1 取消点赞")
    @TableField("operation")
    private Integer operation;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间")
    @TableField("created_time")
    private Date createdTime;


}
