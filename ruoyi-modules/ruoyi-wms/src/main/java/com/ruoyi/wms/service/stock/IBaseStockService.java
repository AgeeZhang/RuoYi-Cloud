package com.ruoyi.wms.service.stock;

import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.wms.domain.BaseStock;
import com.ruoyi.wms.domain.vo.StockVo;

import java.util.List;

/**
 * 基本库存Service接口
 *
 * @author ryas
 * created on 2024-02-22
 */
public interface IBaseStockService {

    /**
     * 查询基本库存
     *
     * @param whsCd 基本库存主键
     * @return 基本库存
     */
    BaseStock selectBaseStockByPK(String whsCd, String stgBinCd, String itemCd, String lotNo, String subLotNo);

    /**
     * 查询基本库存列表
     *
     * @param baseStock 基本库存
     * @return 基本库存集合
     */
    List<BaseStock> selectBaseStockList(BaseStock baseStock);

    /**
     * 入库
     *
     * @param form 库存数据
     * @return 结果
     */
    AjaxResult instock(StockVo form) throws Exception;

    /**
     * 出库
     *
     * @param form 库存数据
     * @return 结果
     */
    AjaxResult outstock(StockVo form) throws Exception;

}
