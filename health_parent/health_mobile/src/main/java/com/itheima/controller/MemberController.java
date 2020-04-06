package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.Map;

/**
 * 会员服务
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private MemberService memberService;


    //使用手机号和验证码登录
    @RequestMapping("/login")
    public Result login(@RequestBody Map map){
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        //从Redis中获取缓存的验证码
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);

        if (validateCode != null && codeInRedis != null && validateCode.equals(codeInRedis)){
            //输入正确
            //判断用户是否是会员
            Member member = memberService.findByTelephone(telephone);
            if (member == null){
                //还不是会员,自动完成注册
                member = new Member();
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                memberService.add(member);
            }
            //登录成功
            //写入cookie,跟踪用户
            Cookie cookie = new Cookie("login_member_telephone",telephone);
            cookie.setPath("/");//路径
            cookie.setMaxAge(60*60*24*30);//设置保存时间
            //将会员信息保存到Redis
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone,60*30,json);
            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        }else {
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
    }
}
