package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;
import java.util.UUID;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    private SetmealService setmealService;

    @Autowired
    private JedisPool jedisPool;

    //图片上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        try {
            //获取原始文件名
            String originalFilename = imgFile.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            //获取文件后缀
            String suffix = originalFilename.substring(lastIndexOf);
            //使用UUID随机产生文件名称,防止文件覆盖
            String fileName = UUID.randomUUID().toString() +suffix;

            /*System.out.println(fileName);
            System.out.println(imgFile.getBytes());

            System.out.println(QiniuUtils.accessKey);
            System.out.println(QiniuUtils.secretKey);
            System.out.println(QiniuUtils.bucket);

            QiniuUtils.deleteFileFromQiniu("http://q7r8u35yg.bkt.clouddn.com/05c4a34d-3b7b-4b2e-a7b7-d8576b398163.jpg");

            QiniuUtils.upload2Qiniu("D:\\黑马\\第十二阶段—传智健康\\项目资料\\day04\\素材\\图片资源\\03a36073-a140-4942-9b9b-712cecb144901.jpg","abc.jpg");
*/
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            //图片上传成功
            Result result = new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS);
            result.setData(fileName);
            //将上传图片名称存入Redis,基于Redis的Set集合储存
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            return result;

        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    //新增
    @PostMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        try {
            setmealService.add(setmeal,checkgroupIds);
            return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    //分页查询
    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = setmealService.findPage(queryPageBean);
        return pageResult;
    }

    /*public static void main(String[] args) {
        String user = "zhangsan";
        String fileName = "123.jsp";
        byte[] bytes = user.getBytes();
        QiniuUtils.upload2Qiniu(bytes,fileName);
        System.out.println("调用了");
    }*/
}
