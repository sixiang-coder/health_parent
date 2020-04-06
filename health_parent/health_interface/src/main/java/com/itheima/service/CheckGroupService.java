package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;

import java.util.List;

/**
 * 检查组服务接口
 */
public interface CheckGroupService {
    void add(CheckGroup checkGroup,Integer[] checkitemIds);
    PageResult pageQuery(QueryPageBean queryPageBean);
    CheckGroup findById(Integer id);
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);
    void edit(CheckGroup checkGroup,Integer[] checkitemIds);
    void delete(Integer id);
    List<CheckGroup> findAll();
}
