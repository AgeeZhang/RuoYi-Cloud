package com.xjs.job.task;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.domain.R;
import com.xjs.business.api.RemoteCopyWritingFeign;
import com.xjs.business.api.domain.CopyWriting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author xiejs
 * @desc  调用文案定时任务
 * @create 2021-12-27
 */
@Component("CopyWritingTask")
public class CopyWritingTask {

    @Resource
    private RemoteCopyWritingFeign remoteCopyWritingFeign;

    private static final Logger log = LoggerFactory.getLogger(CopyWritingTask.class);

    /**
     * 任务执行
     */
    public void execute() {
        LocalDateTime localDateTime1 = DateUtil.date().toLocalDateTime();
        log.info("---------------文案定时任务Start-------------------");
        R<CopyWriting> r = remoteCopyWritingFeign.copyWriting();
        log.info("文案定时任务结果:code={},msg={},data={}",r.getCode(),r.getMsg(),r.getData());
        LocalDateTime localDateTime2 = DateUtil.date().toLocalDateTime();
        long between = ChronoUnit.MILLIS.between(localDateTime1, localDateTime2);
        log.info("Job耗费时间:{}ms", between);
        log.info("---------------文案定时任务end---------------------");
    }
}
