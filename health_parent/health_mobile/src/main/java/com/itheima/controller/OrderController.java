package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;


/**
 * 提检预约
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    JedisPool jedisPool;
    @Reference
    private OrderService orderService;
    

    @PostMapping("/submit")
    public Result submit(@RequestBody Map map){
        String telephone = (String)map.get("telephone");
        //从Redis中获取保存的验证码
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String)map.get("validateCode");

        //将用户输入的验证码和Redis中保存的进行比对
        if (validateCodeInRedis != null && validateCode != null && validateCode.equals(validateCodeInRedis)){
            //比对成功,调用服务完成预约业务处理
            map.put("orderType", Order.ORDERTYPE_WEIXIN);//设置预约类型，分为微信预约、电话预约
            Result result = new Result(false, "预约失败");
            try {
                //通过Dubbo远程调用服务实现在线预约业务处理
                result = orderService.order(map);
            }catch (Exception e){
                e.printStackTrace();
                return result;
            }
            if (result.isFlag()) {
                //预约成功,可以为用户发送短信
                try {
                    //随机生成六位动态码
                    Integer orderCode = ValidateCodeUtils.generateValidateCode(6);
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE, telephone, orderCode.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return result;
        }else {
            //如果比对不成功,返回结果给页面
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
    }

    /**
     * 根据id查询预约信息，包括套餐信息和会员信息
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
