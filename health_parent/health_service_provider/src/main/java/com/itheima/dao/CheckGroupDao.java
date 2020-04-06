package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import java.util.List;
import java.util.Map;

/**
 * 检查组持久层接口
 */
public interface CheckGroupDao {
    void add(CheckGroup checkGroup);
    void setCheckGroupAndCheckItem(Map map);
    Page<CheckGroup> findByCondition(String queryString);
    CheckGroup findById(Integer id);
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);
    void deleteAssociation(Integer id);
    void edit(CheckGroup checkGroup);
    void delete(Integer id);
    List<CheckGroup> findAll();
}
