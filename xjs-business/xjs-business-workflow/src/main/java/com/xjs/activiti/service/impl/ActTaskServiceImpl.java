

package com.xjs.activiti.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.xjs.activiti.domain.ActWorkflowFormData;
import com.xjs.activiti.domain.dto.ActTaskDTO;
import com.xjs.activiti.domain.dto.ActWorkflowFormDataDTO;
import com.xjs.activiti.service.IActTaskService;
import com.xjs.activiti.service.IActWorkflowFormDataService;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskQuery;
import org.activiti.runtime.api.model.impl.APITaskConverter;
import org.activiti.runtime.api.query.impl.PageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Activiti任务服务接口
 *
 * @author xiejs
 * @since 2022-04-17 01:54:51
 */

@Service
public class ActTaskServiceImpl implements IActTaskService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IActWorkflowFormDataService actWorkflowFormDataService;
    @Autowired
    private APITaskConverter taskConverter;
    @Autowired
    private HistoryService historyService;


    @Override
    public Page<ActTaskDTO> selectTaskList(PageDomain pageDomain) {
        Page<ActTaskDTO> list = new Page<ActTaskDTO>();

        //org.activiti.api.runtime.shared.query.Page<Task> pageTasks = taskRuntime
        //        .tasks(Pageable.of((pageDomain.getPageNum() - 1) * pageDomain.getPageSize(), pageDomain.getPageSize()));

        org.activiti.api.runtime.shared.query.Page<Task> taskQuery = this.createTaskQuery(pageDomain);
        List<Task> tasks = taskQuery.getContent();

        int totalItems = taskQuery.getTotalItems();
        list.setTotal(totalItems);
        if (totalItems != 0) {
            Set<String> processInstanceIdIds = tasks.parallelStream().map(Task::getProcessInstanceId).collect(Collectors.toSet());
            List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIdIds).list();

            List<ActTaskDTO> actTaskDTOS = tasks.stream()
                    .map(t -> {
                        ActTaskDTO actTaskDTO = new ActTaskDTO(t, processInstanceList.parallelStream()
                                .filter(pi ->
                                        t.getProcessInstanceId().equals(pi.getId())).findAny().get());

                        //根据开始时间获取历史任务中的办理人
                        List<HistoricTaskInstance> instanceList = historyService.createHistoricTaskInstanceQuery()
                                .processInstanceId(actTaskDTO.getProcessInstanceId())
                                .orderByHistoricTaskInstanceStartTime()
                                .asc()
                                .list();
                        if (CollUtil.isNotEmpty(instanceList)) {
                            actTaskDTO.setAssignee(instanceList.get(0).getAssignee());
                        }

                        return actTaskDTO;
                    })
                    .collect(Collectors.toList());
            list.addAll(actTaskDTOS);
        }
        return list;
    }

    @Override
    public List<String> formDataShow(String taskID) {
        //Task task = taskRuntime.task(taskID);

        org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskID).singleResult();

        UserTask userTask;
        if (task == null) {
            HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery().taskId(taskID).singleResult();
            userTask = (UserTask) repositoryService.getBpmnModel(historicTask.getProcessDefinitionId()).getFlowElement(historicTask.getFormKey());

        } else {
            userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId()).getFlowElement(task.getFormKey());
        }

/*  ------------------------------------------------------------------------------
            FormProperty_0ueitp2--__!!类型--__!!名称--__!!是否参数--__!!默认值
            例子：
            FormProperty_0lovri0--__!!string--__!!姓名--__!!f--__!!同意!!__--驳回
            FormProperty_1iu6onu--__!!int--__!!年龄--__!!s

            默认值：无、字符常量、FormProperty_开头定义过的控件ID
            是否参数：f为不是参数，s是字符，t是时间(不需要int，因为这里int等价于string)
            注：类型是可以获取到的，但是为了统一配置原则，都配置到
            */

        //注意!!!!!!!!:表单Key必须要任务编号一模一样，因为参数需要任务key，但是无法获取，只能获取表单key“task.getFormKey()”当做任务key

        if (userTask == null) {
            return null;
        }
        List<FormProperty> formProperties = userTask.getFormProperties();
        return formProperties.stream().map(BaseElement::getId).collect(Collectors.toList());

    }

    @Override
    public int formDataSave(String taskID, List<ActWorkflowFormDataDTO> awfs) throws ParseException {
        //Task task = taskRuntime.task(taskID);

        org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskID).singleResult();

        /*org.activiti.engine.task.Task task = taskService.createTaskQuery().taskCandidateOrAssigned(authenticatedUserId,
                userGroups).taskId(taskId).singleResult();*/

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

        HashMap<String, Object> variables = new HashMap<String, Object>();
        //前端传来的字符串，拆分成每个控件
        List<ActWorkflowFormData> acwfds = new ArrayList<>();
        for (ActWorkflowFormDataDTO awf : awfs) {
            ActWorkflowFormData actWorkflowFormData = new ActWorkflowFormData(processInstance.getBusinessKey(), awf, task);
            acwfds.add(actWorkflowFormData);
            //构建参数集合
            if (!"f".equals(awf.getControlIsParam())) {
                variables.put(awf.getControlId(), awf.getControlValue());
            }
        }//for结束
        if (task.getAssignee() == null) {
            //taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());

            String username = SecurityUtils.getUsername();
            taskService.claim(task.getId(), username);

        }
        //带参数完成任务
        //taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskID).withVariables(variables).build());

        taskService.complete(task.getId(), variables, true);

        //taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskID).build());

        //写入数据库
        return actWorkflowFormDataService.insertActWorkflowFormDatas(acwfds);
    }

    @Override
    public Page<ActTaskDTO> selectHistoryTaskList(PageDomain pageDomain) {
        Page<ActTaskDTO> list = new Page<ActTaskDTO>();

        Pageable pageable = Pageable.of((pageDomain.getPageNum() - 1) * pageDomain.getPageSize(), pageDomain.getPageSize());
        String username = SecurityUtils.getUsername();
        List<String> postCode = SecurityUtils.getLoginUser().getSysUser().getPostCode();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .or()
                .taskAssignee(username)
                .taskOwner(username)
                .taskCandidateUser(username, postCode)
                .endOr();

        List<HistoricTaskInstance> taskInstanceList = taskInstanceQuery.orderByHistoricTaskInstanceStartTime().desc().listPage(pageable.getStartIndex(), pageable.getMaxItems());

        Set<String> processInstanceIdIds = taskInstanceList.parallelStream().map(HistoricTaskInstance::getProcessInstanceId).collect(Collectors.toSet());
        List<HistoricProcessInstance> processInstanceList = new ArrayList<>();
        if (CollUtil.isNotEmpty(processInstanceIdIds)) {
            processInstanceList = historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdIds).list();
        }

        List<HistoricProcessInstance> finalProcessInstanceList = processInstanceList;
        List<ActTaskDTO> actTaskDTOS = taskInstanceList.stream()
                .map(t ->

                        new ActTaskDTO(t, finalProcessInstanceList.parallelStream()
                                .filter(pi ->
                                        t.getProcessInstanceId().equals(pi.getId())).findAny().orElse(new HistoricProcessInstanceEntityImpl())
                        )
                )
                .collect(Collectors.toList());
        list.addAll(actTaskDTOS);

        long count = taskInstanceQuery.count();
        list.setTotal(count);

        return list;
    }


    /**
     * task创建查询
     *
     * @param pageDomain 分页参数
     * @return 分页数据
     */
    private org.activiti.api.runtime.shared.query.Page<Task> createTaskQuery(PageDomain pageDomain) {
        Pageable pageable = Pageable.of((pageDomain.getPageNum() - 1) * pageDomain.getPageSize(), pageDomain.getPageSize());
        String username = SecurityUtils.getUsername();
        List<String> postCode = SecurityUtils.getLoginUser().getSysUser().getPostCode();
        TaskQuery taskQuery = taskService.createTaskQuery()
                .or()
                //待办任务根据 组 查询
                .taskCandidateOrAssigned(username, postCode)
                .taskOwner(username)
                .endOr();

        List<org.activiti.engine.task.Task> list = taskQuery.listPage(pageable.getStartIndex(), pageable.getMaxItems());
        List<Task> tasks = taskConverter.from(list);
        return new PageImpl<>(tasks, Math.toIntExact(taskQuery.count()));
    }
}