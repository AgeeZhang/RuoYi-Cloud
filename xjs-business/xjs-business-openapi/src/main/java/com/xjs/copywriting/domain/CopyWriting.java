package com.xjs.copywriting.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.annotation.Excel;
import com.xjs.validation.group.SelectGroup;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author xiejs
 * @desc  文案实体类
 * @create 2021-12-27
 */
@TableName("api_copywriting")
@Data
public class CopyWriting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /** 文案内容 */
    @Excel(name = "文案内容")
    @Size(max = 100, message = "请控制文案内容长度在100字符", groups = {SelectGroup.class})
    private String content;

    /** 文案来源 */
    @Excel(name = "文案来源")
    @Size(max = 50, message = "请控制文案来源长度在50字符", groups = {SelectGroup.class})
    private String source;

    @Excel(name = "创建时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(exist = false)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endCreateTime;

    @Excel(name = "文案类型" ,readConverterExp = "1=朋友圈文案,2=网易云热评,3=经典语句,4=名人名言")
    private Integer type;
}