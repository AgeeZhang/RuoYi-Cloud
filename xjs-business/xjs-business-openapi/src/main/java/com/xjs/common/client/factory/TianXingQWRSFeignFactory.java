package com.xjs.common.client.factory;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.xjs.common.client.api.tianxing.TianXingQWRSFeignClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import static com.xjs.consts.ApiConst.DEMOTE_ERROR;

/**
 * @author xiejs
 * @since 2022-01-10
 */
@Component
@Log4j2
public class TianXingQWRSFeignFactory implements FallbackFactory<TianXingQWRSFeignClient> {

    @Override
    public TianXingQWRSFeignClient create(Throwable cause) {
        log.error("api模块天行全网热搜榜服务调用失败:{},执行降级处理", cause.getMessage());
        return key -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(DEMOTE_ERROR, R.FAIL);
            return jsonObject;
        };
    }
}
