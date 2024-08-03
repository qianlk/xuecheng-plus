package com.xuecheng.content;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Qianlk
 */
@SpringBootTest
public class FreemarkerTest {

    @Autowired
    CoursePublishService coursePublishService;

    @Test
    void testTemplateHtml() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 加载模板
        String classpath = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        configuration.setDefaultEncoding("utf-8");

        // 指定模版文件名称
        Template template = configuration.getTemplate("course_template.ftl");
        // 准备数据
        CoursePreviewDto previewDto = coursePublishService.getCoursePreviewInfo(2L);
        Map<String, Object> map = new HashMap<>();
        map.put("model", previewDto);
        // 静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\Temp\\test.html");
        IOUtils.copy(inputStream, fileOutputStream);
    }
}
