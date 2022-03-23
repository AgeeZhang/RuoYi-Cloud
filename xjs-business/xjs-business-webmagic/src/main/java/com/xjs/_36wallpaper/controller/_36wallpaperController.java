package com.xjs._36wallpaper.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.xjs._36wallpaper.pojo._36wallpaper;
import com.xjs._36wallpaper.service._36wallpaperService;
import com.xjs._36wallpaper.task._36wallpaperTask;
import com.xjs.web.MyBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 36壁纸网爬虫controller
 *
 * @author xiejs
 * @since 2022-02-20
 */
@RestController
@RequestMapping("_36wallpaper")
@Api(tags = "爬虫模块-36壁纸网")
public class _36wallpaperController extends MyBaseController<_36wallpaper> {

    @Autowired
    private _36wallpaperTask wallpaperTask;

    @Autowired
    private _36wallpaperService wallpaperService;


    @GetMapping("getSettings")
    @ApiOperation("获取参数配置")
    @RequiresPermissions("webmagic:_36wallpaper:list")
    public AjaxResult getSettings() {
        JSONObject jsonObject = wallpaperService.getSettings();
        if (Objects.nonNull(jsonObject)) {
            return AjaxResult.success(jsonObject);
        }
        return AjaxResult.error();
    }


    @PutMapping("updateSettings")
    @ApiOperation("修改参数配置")
    @RequiresPermissions("webmagic:_36wallpaper:update")
    public AjaxResult updateSettings(@RequestParam("json") String json) {
        boolean b=wallpaperService.updateSettings(json);
        return toAjax(b);
    }

    @PutMapping("reset")
    @ApiOperation("重置参数配置")
    @RequiresPermissions("webmagic:_36wallpaper:update")
    public AjaxResult resetSettings() {
        boolean b=wallpaperService.resetSettings();
        return toAjax(b);
    }

    @GetMapping("list")
    @ApiOperation("获取壁纸列表")
    @RequiresPermissions("webmagic:_36wallpaper:list")
    public AjaxResult list(_36wallpaper wallpaper) {
        IPage<_36wallpaper> list=wallpaperService.selectWallpaperList(startPageMP(),wallpaper);
        return AjaxResult.success(list);
    }

    @GetMapping("getType")
    @ApiOperation("获取壁纸类别")
    @RequiresPermissions("webmagic:_36wallpaper:list")
    public AjaxResult getType() {
        List<Object> list = wallpaperService.getType();
        return AjaxResult.success(list);
    }




    //----------------------远程rpc调用---------------------------
    @GetMapping("taskForPRC")
    @ApiOperation("供定时任务服务RPC远程调用")
    public R _36wallpaperTaskForPRC() {
        Long count = wallpaperTask.reptileWallpaper();
        return R.ok(count);
    }
}