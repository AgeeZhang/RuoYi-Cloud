package com.xjs.activiti.controller;


import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.core.web.page.TableSupport;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.xjs.activiti.domain.dto.ProcessDefinitionDTO;
import com.xjs.activiti.service.IProcessDefinitionService;
import com.xjs.validation.group.SelectGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("processDefinition")
@Api(tags = "工作流-流程定义")
public class ProcessDefinitionController extends BaseController {

    @Autowired
    private IProcessDefinitionService processDefinitionService;


    /**
     * 获取流程定义集合
     *
     * @param processDefinition
     * @return
     */
    @GetMapping(value = "/list")
    @ApiOperation("获取流程定义集合")
    @RequiresPermissions("activiti:definition")
    public TableDataInfo list(@Validated(SelectGroup.class) ProcessDefinitionDTO processDefinition) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        return getDataTable(processDefinitionService.selectProcessDefinitionList(processDefinition, pageDomain));

    }


    @GetMapping(value = "/getDefinitions/{instanceId}")
    @ApiOperation("按实例 ID 获取定义")
    @RequiresPermissions("activiti:definition")
    public AjaxResult getDefinitionsByInstanceId(@PathVariable("instanceId") String instanceId) {
        return AjaxResult.success(processDefinitionService.getDefinitionsByInstanceId(instanceId));
    }

    /**
     * 删除流程定义
     *
     * @param deploymentId
     * @return
     */
    @Log(title = "流程定义管理", businessType = BusinessType.DELETE)
    @DeleteMapping(value = "/remove/{deploymentId}")
    @ApiOperation("删除流程定义")
    @RequiresPermissions("activiti:definition")
    public AjaxResult delDefinition(@PathVariable("deploymentId") String deploymentId) {
        return toAjax(processDefinitionService.deleteProcessDefinitionById(deploymentId));
    }

    /**
     * 上传并部署流程定义
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Log(title = "流程定义管理", businessType = BusinessType.IMPORT)
    @PostMapping(value = "/uploadStreamAndDeployment")
    @ApiOperation("上传并部署流程定义")
    public AjaxResult uploadStreamAndDeployment(@RequestParam("file") MultipartFile file) throws IOException {
        processDefinitionService.uploadStreamAndDeployment(file);
        return AjaxResult.success();

    }

    /**
     * 启动挂起流程流程定义
     *
     * @param processDefinition
     * @return
     */
    @Log(title = "流程定义管理", businessType = BusinessType.UPDATE)
    @PostMapping("/suspendOrActiveApply")
    @ApiOperation("启动挂起流程流程定义")
    public AjaxResult suspendOrActiveApply(@RequestBody ProcessDefinitionDTO processDefinition) {
        processDefinitionService.suspendOrActiveApply(processDefinition.getId(), processDefinition.getSuspendState());
        return AjaxResult.success();
    }

    /**
     * 上传流程流程定义
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @Log(title = "流程定义管理", businessType = BusinessType.IMPORT)
    @PostMapping(value = "/upload")
    @ApiOperation("上传流程流程定义")
    public AjaxResult upload(@RequestParam("processFile") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = processDefinitionService.upload(multipartFile);
            return AjaxResult.success("操作成功", fileName);

        }
        return AjaxResult.error("不允许上传空文件！");
    }


    /**
     * 通过stringBPMN添加流程定义
     *
     * @param stringBPMN
     * @return
     */
    @PostMapping(value = "/addDeploymentByString")
    @ApiOperation("通过stringBPMN添加流程定义")
    public AjaxResult addDeploymentByString(@RequestParam("stringBPMN") String stringBPMN) {
        processDefinitionService.addDeploymentByString(stringBPMN);
        return AjaxResult.success();

    }


    /**
     * 获取流程定义XML
     *
     * @param response
     * @param deploymentId
     * @param resourceName
     */
    @GetMapping(value = "/getDefinitionXML")
    public void getProcessDefineXML(HttpServletResponse response,
                                    @RequestParam("deploymentId") String deploymentId,
                                    @RequestParam("resourceName") String resourceName) throws IOException {

        processDefinitionService.getProcessDefineXML(response, deploymentId, resourceName);
    }


}