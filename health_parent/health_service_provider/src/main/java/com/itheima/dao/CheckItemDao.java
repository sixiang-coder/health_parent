package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;

import java.util.List;

/**
 * 持久层接口
 */
public interface CheckItemDao {
    //新增
    public void add(CheckItem checkItem);

    //分页查询
    public Page<CheckItem> selectByCondition(String queryString);

    public long findCountByCheckItemId(Integer checkItemId);

    //删除检查项
    public void deleteById(Integer id);

    //编辑检查项
    public void edit(CheckItem checkItem);

    //根据id查询检查项
    public CheckItem findById(Integer id);

    public List<CheckItem> findAll();
}
