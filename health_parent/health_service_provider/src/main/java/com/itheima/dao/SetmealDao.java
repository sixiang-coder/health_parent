package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Setmeal;
import com.sun.xml.bind.v2.model.core.ID;

import java.util.List;
import java.util.Map;

public interface SetmealDao {

    public void add(Setmeal setmeal);
    public void setSetmealAndCheckGroup(Map<String,Integer> map);
    public Page<Setmeal> selectByCondition(String queryString);
    public List<Setmeal> findAll();
    public Setmeal findById(Integer id);
    public Setmeal findSetmealById(Integer id);
    public List<Map<String,Object>> findSetmealCount();
}
