package com.ruoyi.wms.service.master;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.domain.ExtBaseEntity;
import com.ruoyi.common.security.utils.SecurityUtilsExt;
import com.ruoyi.system.api.RemoteFileService;
import com.ruoyi.system.api.domain.SysFileInfo;
import com.ruoyi.wms.domain.ItemInfo;
import com.ruoyi.wms.mapper.master.ItemInfoDynamicSqlSupport;
import com.ruoyi.wms.mapper.master.ItemInfoExtMapper;
import com.ruoyi.wms.mapper.master.ItemInfoMapper;
import jakarta.annotation.Resource;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * 物品基础信息Service业务层处理
 *
 * @author ryas
 * created on 2024-02-20
 */
@Service
public class ItemInfoServiceImpl implements IItemInfoService {
    @Resource
    private ItemInfoMapper itemInfoMapper;
    @Resource
    private ItemInfoExtMapper itemInfoExtMapper;
    @Resource
    private RemoteFileService remoteFileService;

    /**
     * 查询物品基础信息
     *
     * @param itemCd 物品基础信息主键
     * @return 物品基础信息
     */
    @Override
    public ItemInfo selectItemInfoByItemCd(String itemCd) {
        ItemInfo queryForm = new ItemInfo();
        queryForm.setItemCd(itemCd);
        List<ItemInfo> result = itemInfoExtMapper.selectPageList(queryForm);
        return result.isEmpty() ? null : result.getFirst();
    }

    /**
     * 查询物品基础信息列表
     *
     * @param itemInfo 物品基础信息
     * @return 物品基础信息
     */
    @Override
    public List<ItemInfo> selectItemInfoList(ItemInfo itemInfo) {
        return itemInfoExtMapper.selectPageList(itemInfo);
    }

    /**
     * 新增物品基础信息
     *
     * @param item 物品基础信息
     * @return 结果
     */
    @Transactional
    @Override
    public AjaxResult insertItemInfo(ItemInfo item) {
        //检查物品代码是否已存在
        ItemInfo oldRecord = checkItemCdExists(item.getItemCd(), true);
        if (oldRecord != null) {
            //存在未删除的记录
            return AjaxResult.error("物品代码[" + item.getItemCd() + "]已存在");
        }
        //上传图片文件
        String uploadErrMsg = uploadItemImage(item);
        if (StringUtils.isNotBlank(uploadErrMsg)) {
            return AjaxResult.error(uploadErrMsg);
        }
        //存DB
        int affectedRows = itemInfoMapper.insertSelective(item);
        return affectedRows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改物品基础信息
     *
     * @param item 物品基础信息
     * @return 结果
     */
    @Transactional
    @Override
    public AjaxResult updateItemInfo(ItemInfo item) {
        //上传图片文件
        String uploadErrMsg = uploadItemImage(item);
        if (StringUtils.isNotBlank(uploadErrMsg)) {
            return AjaxResult.error(uploadErrMsg);
        }
        //存DB
        int affectedRows = itemInfoMapper.updateByPrimaryKeySelective(item);
        return affectedRows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 批量删除物品基础信息
     *
     * @param itemCds 需要删除的物品基础信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteItemInfoByItemCds(String[] itemCds) {
        String userId = SecurityUtilsExt.getUserIdStr();
        UpdateStatementProvider provider = SqlBuilder.update(ItemInfoDynamicSqlSupport.itemInfo)
                .set(ItemInfoDynamicSqlSupport.deleteFlag).equalTo(ExtBaseEntity.DELETED)
                .set(ItemInfoDynamicSqlSupport.updateTime).equalTo(DateUtils.getNowDate())
                .set(ItemInfoDynamicSqlSupport.updateBy).equalTo(userId)
                .where(ItemInfoDynamicSqlSupport.itemCd, SqlBuilder.isIn(itemCds))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return itemInfoMapper.update(provider);
    }

    /**
     * 删除物品基础信息信息
     *
     * @param itemCd 物品基础信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteItemInfoByItemCd(String itemCd) {
        ItemInfo record = new ItemInfo();
        record.setItemCd(itemCd);
        record.setDeleteFlag(ExtBaseEntity.DELETED);
        record.setUpdateTime(DateUtils.getNowDate());
        return itemInfoMapper.updateByPrimaryKey(record);
    }

    /**
     * 检查物品代码是否已存在
     *
     * @param itemCd               物品代码
     * @param deleteIfLogicDeleted 如果是已经逻辑删除的数据，则是否顺带物理删除掉（如果为true则逻辑删除的也会返回null）
     * @return null:不存在; not null:存在
     */
    @Transactional
    @Override
    public ItemInfo checkItemCdExists(String itemCd, boolean deleteIfLogicDeleted) {
        Optional<ItemInfo> itemInfo = itemInfoMapper.selectOne(dsl -> dsl.where(ItemInfoDynamicSqlSupport.itemCd, SqlBuilder.isEqualTo(itemCd)));
        if (itemInfo.isEmpty()) {
            return null;
        } else {
            ItemInfo result = itemInfo.get();
            if (deleteIfLogicDeleted && result.isLogicDeleted()) {
                //顺带物理删除掉
                itemInfoMapper.deleteByPrimaryKey(result.getItemCd());
                return null;
            }
            return result;
        }
    }

    /**
     * 上传图片文件
     *
     * @return 错误消息，成功返回null
     */
    private String uploadItemImage(ItemInfo item) {
        if (item.getItemImages() == null){
            return null;
        }
        for (MultipartFile file : item.getItemImages()) {
            R<SysFileInfo> fileResult = remoteFileService.upload(file);
            if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
                //上传失败
                if (fileResult == null || StringUtils.isBlank(fileResult.getMsg())) {
                    return "文件服务异常，请联系管理员";
                } else {
                    return "文件服务异常，" + fileResult.getMsg();
                }
            } else {
                //上传成功
                item.setPictureId(fileResult.getData().getFileId());
                item.setPictureUrl(fileResult.getData().getUrl());
            }
        }
        return null;
    }
}
