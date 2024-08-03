package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

/**
 * fallback降级处理
 * @author Qianlk
 */
public class MediaServiceClientFallback implements MediaServiceClient {
    @Override
    public String upload(MultipartFile filedata, String objectName) {
        return "发生异常";
    }
}
