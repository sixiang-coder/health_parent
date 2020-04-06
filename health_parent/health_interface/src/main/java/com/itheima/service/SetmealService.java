package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

/**
 * 体检套餐服务接口
 */
public interface SetmealService {

    public void add(Setmeal setmeal,Integer[] checkgroupIds);
    public PageResult findPage(QueryPageBean queryPageBean);
    public List<Setmeal> findAll();
    public Setmeal findById(Integer id);
    public Setmeal findSetmealById(Integer id);
    public List<Map<String,Object>> findSetmealCount();
}
