package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * FallbackFactory降级处理,可以拿到熔断异常
 *
 * @author Qianlk
 */
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            @Override
            public String upload(MultipartFile filedata, String objectName) {
                //降级方法
                log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}", throwable.toString(), throwable);
                return null;
            }
        };

    }
}
