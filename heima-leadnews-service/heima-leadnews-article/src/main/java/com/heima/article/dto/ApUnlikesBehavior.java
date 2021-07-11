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
 * APP不喜欢行为表
 * </p>
 *
 * @author mcm
 * @since 2021-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ap_unlikes_behavior")
@ApiModel(description="APP不喜欢行为表")
public class ApUnlikesBehavior implements Serializable {

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
     * 0 不喜欢
            1 取消不喜欢
     */
    @ApiModelProperty(value = "0 不喜欢            1 取消不喜欢")
    @TableField("type")
    private Integer type;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间")
    @TableField("created_time")
    private Date createdTime;


}
