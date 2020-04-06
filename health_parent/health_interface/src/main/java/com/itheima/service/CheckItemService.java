package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;

import java.util.List;

public interface CheckItemService {

    //服务接口
    //新增检查项
    public void add(CheckItem checkItem);

    //分页查询
    public PageResult pageQuery(QueryPageBean queryPageBean);

    //删除检查项
    public void deleteById(Integer id);

    //编辑检查项
    public void edit(CheckItem checkItem);

    //根据id查询检查项
    public CheckItem findById(Integer id);

    public List<CheckItem> findAll();
}
