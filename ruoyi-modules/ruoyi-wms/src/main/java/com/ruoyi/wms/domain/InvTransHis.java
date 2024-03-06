package com.ruoyi.wms.domain;

import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.ExtBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This class was generated by MyBatis Generator.
 *
 * <ul>
 *   <li> Table: WMS_B_INV_TRANS_HIS </li>
 *   <li> Remarks: 入出库履历表 </li>
 * </ul>
 *
 * @author ryas
 * created on 2024-02-22
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class InvTransHis extends ExtBaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //==================== ↓↓↓↓↓↓ 非表字段 ↓↓↓↓↓↓ ====================

    /**
     * 创建者用户名
     */
    private String createByUser;

    /**
     * 标准单位代码
     */
    private String stdUnitCd;

    /**
     * 标准单位名称
     */
    private String stdUnitName;

    /**
     * 包装单位代码
     */
    private String pkgUnitCd;

    /**
     * 包装单位名称
     */
    private String pkgUnitName;

    //==================== ↓↓↓↓↓↓ 表字段 ↓↓↓↓↓↓ ====================

    /**
     * 入出库履历号
     */
    @Excel(name = "入出库履历号", sort = 1)
    private String invTransNo;

    /**
     * 从属部门ID
     */
    private Integer deptId;

    /**
     * 入出库类型(1:入库,2:出库)
     */
    private Integer invTransType;

    /**
     * 仓库代码
     */
    private String whsCd;

    /**
     * 货架号
     */
    private String stgBinCd;

    /**
     * 托盘ID
     */
    private String palletId;

    /**
     * 标准单位数量
     */
    private BigDecimal stdUnitQty;

    /**
     * 包装单位数量
     */
    private BigDecimal pkgUnitQty;

    /**
     * 交易单号
     */
    private String transOrderNo;

    /**
     * 交易单明细号
     */
    private String transOrderDetlNo;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 业务区分
     */
    private String businessCls;

    /**
     * 物品代码
     */
    private String itemCd;

    /**
     * 批号
     */
    private String lotNo;

    /**
     * 子批号
     */
    private String subLotNo;

    /**
     * 序列号
     */
    private String serialNo;

    /**
     * 入出库理由
     */
    private String reason;

    /**
     * 备注1
     */
    private String remark1;

    /**
     * 备注2
     */
    private String remark2;

    /**
     * 备注3
     */
    private String remark3;

    /**
     * 备注4
     */
    private String remark4;

    /**
     * 备注5
     */
    private String remark5;

}