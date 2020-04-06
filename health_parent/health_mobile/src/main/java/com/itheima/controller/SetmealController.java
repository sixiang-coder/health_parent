package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    //查询所有套餐
    @GetMapping("/getAllSetmeal")
    public Result getAllSetmeal(){
        try {
            List<Setmeal> list = setmealService.findAll();
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS,list);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }

    //根据id查询套餐详细信息
    @PostMapping("/findById")
    public Result findById(Integer id){
        try {
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        }catch (Exception e){
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    @PostMapping("/findSetmealById")
    public Result findSetmealById(Integer id){
        try {
            Setmeal setmeal = setmealService.findSetmealById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        }catch (Exception e){
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }
}
