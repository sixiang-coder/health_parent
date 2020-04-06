package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检查组管理
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {
    @Reference
    private CheckGroupService checkGroupService;

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        try {
            checkGroupService.add(checkGroup,checkitemIds);
        }catch (Exception e){
            //新增失败
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        //新增成功
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    //分页查询
    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = checkGroupService.pageQuery(queryPageBean);
        return pageResult;
    }

    //根据id查询检查组
    @GetMapping("/findById")
    public Result findById(Integer id){
        CheckGroup checkGroup = checkGroupService.findById(id);
        if(checkGroup != null) {
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS, checkGroup);
        }
        return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);

    }

    //根据检查组合id查询对应的所有检查项id
    @GetMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(Integer id){
        try {
            List<Integer> checkitemIds = checkGroupService.findCheckItemIdsByCheckGroupId(id);
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkitemIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    //更新检查组
    @PutMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        try {
            checkGroupService.edit(checkGroup,checkitemIds);
            return new Result(true,MessageConstant.EDIT_CHECKGROUP_SUCCESS);
        }catch (Exception e){
            return new Result(false,MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
    }

    //删除检查组
    @DeleteMapping("/delete")
    public Result delete(Integer id){
        try {
            checkGroupService.delete(id);
            return new Result(true,MessageConstant.DELETE_CHECKGROUP_SUCCESS);
        }catch (Exception e){
            return new Result(false,MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
    }

    //查询所有检查组
    @PostMapping("/findAll")
    public Result findAll(){
        try {
            List<CheckGroup> list = checkGroupService.findAll();
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,list);
        }catch (Exception e){
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

}
