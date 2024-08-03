package com.xuecheng.content.feignclient;

import com.xuecheng.content.model.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 搜索服务的远程调用接口
 * @author Qianlk
 */
@FeignClient(value = "search", fallbackFactory = SearchServiceClientFallbackFactory.class)
@RequestMapping("/search")
public interface SearchServiceClient {

    @PostMapping("/index/course")
    Boolean add(@RequestBody CourseIndex courseIndex);
}
