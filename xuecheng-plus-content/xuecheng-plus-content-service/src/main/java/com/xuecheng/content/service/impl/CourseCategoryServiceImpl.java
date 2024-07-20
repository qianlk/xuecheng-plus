package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Qianlk
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        // list转map, id作为key
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(CourseCategory::getId, value -> value, (key1, key2) -> key2));

        // 定义返回值
        List<CourseCategoryTreeDto> rt = new ArrayList<>();

        courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                    // 一级
                    if (item.getParentid().equals(id)) {
                        rt.add(item);
                    }
                    // 找到当前节点对应的父节点
                    CourseCategoryTreeDto sup = mapTemp.get(item.getParentid());
                    if (sup != null) {
                        if (sup.getChildrenTreeNodes() == null) {
                            sup.setChildrenTreeNodes(new ArrayList<>());
                        }
                        // 放入父节点的children中
                        sup.getChildrenTreeNodes().add(item);
                    }
                });

        return rt;
    }
}
