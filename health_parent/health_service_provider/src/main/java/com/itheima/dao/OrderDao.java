package com.itheima.dao;

import com.itheima.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    public List<Order> findByCondition(Order order);
    public void add(Order order);
    public Map findByForDetail(Integer id);
    public Integer findCountBySetmealId(Integer setmealId);
    public Integer findOrderCountByDate(String today);
    public Integer findOrderCountAfterDate(String date);
    public Integer findVisitsCountByDate(String today);
    public Integer findVisitsCountAfterDate(String date);
    public List<Map> findHotSetmeal();
}
