package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Qianlk
 */
@Controller
public class FreemarkerController {

    @GetMapping("/test/freemarker")
    public ModelAndView testFreemarker() {
        ModelAndView modelAndView = new ModelAndView();
        // 根据名称+.fit找到模版文件
        modelAndView.setViewName("test");
        modelAndView.addObject("name", "小明");
        return  modelAndView;
    }
}
