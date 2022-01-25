package com.xjs.common.task;

import com.xjs.XjsOpenApiApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiejs
 * @since 2022-01-12
 */
@SpringBootTest(classes = XjsOpenApiApp.class)
class OpenApiTaskTest {
    @Autowired
    private OpenApiTask openApiTask;

    @Test
    void execute() {
        openApiTask.deleteRepeat();
    }
}