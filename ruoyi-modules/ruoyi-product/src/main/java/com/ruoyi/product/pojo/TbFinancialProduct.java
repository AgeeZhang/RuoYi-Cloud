package com.ruoyi.product.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 金融产品表
 * @TableName tb_financial_product
 */
@TableName(value ="tb_financial_product")
@Data
@Document(indexName = "tb_financial_product")
public class TbFinancialProduct implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer productId;

    /**
     * 企业id
     */
    private Integer firmId;

    /**
     * 企业名称
     */
    private String firmName;

    /**
     * 产品名称
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String productName;

    /**
     * 产品描述
     */
    private String productDesc;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 产品种类ID
     */
    private Integer typeId;

    /**
     * 期数
     */
    private Integer periods;

    /**
     * 利率
     */
    private Integer productRate;

    /**
     * 手续费
     */
    private Integer produtCharge;

    /**
     * 最大额度
     */
    private Integer maxPrice;

    /**
     * 最小额度
     */
    private Integer minPrice;

    /**
     * 逾期还款
     */
    private Integer termsForLate;

    /**
     * 允许提前还款
     */
    private String termsProductLate;

    /**
     * 0:没有利息 1:等额本息 2:等额本金
     */
    private Integer modeProduct;

    /**
     * 产品LOGO
     */
    private String productPhoto;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private Integer delFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Field(type = FieldType.Date,format = DateFormat. custom,pattern = "yyy-MM-dd HH:mm: ss")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Field(type = FieldType.Date,format = DateFormat. custom,pattern = "yyy-MM-dd HH:mm: ss")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}