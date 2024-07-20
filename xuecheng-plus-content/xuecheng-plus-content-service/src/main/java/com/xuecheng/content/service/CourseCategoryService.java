package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author Qianlk
 */
public interface CourseCategoryService {

    /**
     * 课程分类树形结构查询
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
