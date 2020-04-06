package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {

    public void add(OrderSetting orderSetting);
    public Long findCountByOrderDate(Date date);
    public void editNumberByOrderDate(OrderSetting orderSetting);
    public List<OrderSetting> getOrderSettingByMonth(Map map);
    public OrderSetting findByOrderDate(Date orderDate);
    public void editReservationsByOrderDate(OrderSetting orderSetting);
}
