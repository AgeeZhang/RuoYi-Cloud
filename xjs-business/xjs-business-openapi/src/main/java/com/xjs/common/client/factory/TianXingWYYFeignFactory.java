package com.xjs.common.client.factory;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.xjs.common.client.api.tianxing.TianXingWYYFeignClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import static com.xjs.consts.ApiConst.DEMOTE_ERROR;

/**
 * @author xiejs
 * @desc  天行数据平台网易云热评接口降级处理
 * @create 2021-12-28
 */
@Log4j2
@Component
public class TianXingWYYFeignFactory implements FallbackFactory<TianXingWYYFeignClient> {
    @Override
    public TianXingWYYFeignClient create(Throwable cause) {
        log.error("api模块朋友圈文案服务调用失败:{},执行降级处理", cause.getMessage());
        return requestBody -> {
            JSONObject jsonObject = new JSONObject();
            //构建一个异常json给下层接口处理
            jsonObject.put(DEMOTE_ERROR, R.FAIL);
            return jsonObject;
        };
    }
}
