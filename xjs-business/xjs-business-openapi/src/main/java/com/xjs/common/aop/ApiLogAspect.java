package com.xjs.common.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.domain.R;
import com.xjs.business.warning.RemoteWarningCRUDFeign;
import com.xjs.business.warning.domain.ApiRecord;
import com.xjs.enums.StatusEnum;
import com.xjs.log.mapper.ApiLogMapper;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * @author xiejs
 * @desc  api日志切面类
 * @create 2021-12-26
 */
@Component
@Aspect
@Log4j2
public class ApiLogAspect {

    @Resource
    private ApiLogMapper apiLogMapper;

    //用来调用预警，记录预警信息
    @Autowired
    private RemoteWarningCRUDFeign remoteWarningCRUDFeign;

    /**
     * 声明AOP签名
     */
    @Pointcut("@annotation(com.xjs.common.aop.ApiLog)")
    public void pointcut() {
    }

    /**
     * 环绕切入
     */
    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            LocalDateTime localDateTime1 = DateUtil.date().toLocalDateTime();
            Object obj = joinPoint.proceed();
            LocalDateTime localDateTime2 = DateUtil.date().toLocalDateTime();
            long between = ChronoUnit.MILLIS.between(localDateTime1, localDateTime2);
            log.info("调用接口耗费时间:{}ms", between);
            //执行预警切入逻辑
            warning(between, joinPoint);
            return obj;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 处理完请求后执行
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(apiLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, ApiLog apiLog, Object jsonResult) {
        this.handleApiLog(joinPoint, apiLog, null, jsonResult);
    }


    /**
     * 异常切点
     * @param joinPoint 连接点
     * @param apiLog 自定义注解
     * @param e 抛出的异常
     */
    @AfterThrowing(value = "@annotation(apiLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, ApiLog apiLog, Exception e) {
        handleApiLog(joinPoint, apiLog, e, null);
    }


    private void handleApiLog(JoinPoint joinPoint, ApiLog apiLog, final Exception e, Object jsonResult) {
        com.xjs.log.domain.ApiLog entity = new com.xjs.log.domain.ApiLog();
        String name = apiLog.name();//请求名称
        entity.setApiName(name);
        String url = apiLog.url();//请求地址
        entity.setUrl(url);
        Object[] args = joinPoint.getArgs();//请求体
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(arg);
        }
        entity.setMethod(apiLog.method());
        entity.setRequest(builder.toString());
        entity.setResponse(Optional.ofNullable(jsonResult).toString());
        if (e != null) {
            entity.setIsSuccess(StatusEnum.ERROR);
        }else {
            entity.setIsSuccess(StatusEnum.SUCCESS);
        }
        apiLogMapper.insert(entity);
    }

    /**
     *  预警切入
     * @param between api接口调用时间
     * @param joinPoint aop连接对象
     */
    private void warning(long between, ProceedingJoinPoint joinPoint) {
        //获取目标类名及方法名
        Signature signature = joinPoint.getSignature();
        String method = signature.getName();
        Class aclass = signature.getDeclaringType();
        Method[] methods = aclass.getMethods();
        //根据目标的方法名判断当前方法
        for (Method thisMethod : methods) {
            if (method.equals(thisMethod.getName())) {
                //拿到当前方法的注解判断是否为apiLog注解
                Annotation[] declaredAnnotations = thisMethod.getDeclaredAnnotations();
                for (Annotation annotation : declaredAnnotations) {
                    if (annotation instanceof ApiLog) {
                        String name = ((ApiLog) annotation).name();
                        String url = ((ApiLog) annotation).url();
                        //根据拿到的url和name查询数据库是否存在，存在则count+1，不存在则add
                        ApiRecord apiRecord = new ApiRecord();
                        apiRecord.setApiName(name);
                        apiRecord.setApiUrl(url);
                        apiRecord.setRequestTime((int) between);
                        R<List<ApiRecord>> listR = remoteWarningCRUDFeign.selectApiRecordList(apiRecord);
                        if (listR.getCode() == R.SUCCESS) {
                            List<ApiRecord> data = listR.getData();
                            if (CollUtil.isEmpty(data)) {
                                //设置初始请求次数
                                apiRecord.setTotalCount(1L);
                                remoteWarningCRUDFeign.saveApiRecord(apiRecord);
                            }else {
                                ApiRecord haveApiRecord = data.get(0);
                                haveApiRecord.setRequestTime((int) between);
                                haveApiRecord.setTotalCount(haveApiRecord.getTotalCount()+1L);
                                remoteWarningCRUDFeign.updateApiRecord(haveApiRecord);
                            }
                        }
                    }
                }
            }
        }


    }

}
