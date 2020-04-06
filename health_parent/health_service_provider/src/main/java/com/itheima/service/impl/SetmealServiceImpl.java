package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 体检套餐服务实现类
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")
    private String outputpath;//从属性文件中读取要生成的html对应的目录

    //新增
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        this.setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        //将图片名称保存到Redis
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());

        //新增套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }

    //生成静态页面
    public void generateMobileStaticHtml(){
        //准备模板文件中所需的数据
        List<Setmeal> setmealList = this.findAll();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情静态页面(多个)
        generateMobileSetmealDetailHtml(setmealList);
    }

    //生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList){
        Map map = new HashMap();
        //为模板提供数据,用于生成静态页面
        map.put("setmealList",setmealList);
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);
    }

    //生成套餐详情静态页面(可能有多个)
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList){
        for (Setmeal setmeal : setmealList){
            Map map = new HashMap();
            map.put("setmeal",setmealDao.findById(setmeal.getId()));
            generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_" + setmeal.getId() +".html",map);

        }
    }

    //通用的方法,用于生成静态页面
    public void generateHtml(String templateName,String htmlPageName,Map map){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();//获得配置对象
        Writer out = null;
        try {
            Template template = configuration.getTemplate(templateName);
            //构造输出流
            out = new FileWriter(new File(outputpath + "/" + htmlPageName));
            //输出文件
            template.process(map,out);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());

    }

    //查询所有套餐
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    //根据id查询套餐详细信息
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    public Setmeal findSetmealById(Integer id){
        return setmealDao.findSetmealById(id);
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    //绑定套餐和检查组的多对多关系
    private void setSetmealAndCheckGroup(Integer id,Integer[] checkgroupIds){
        Map<String,Integer> map = null;
        for (Integer checkgroupId : checkgroupIds){
            map = new HashMap<>();
            map.put("setmealId",id);
            map.put("checkgroupId",checkgroupId);
            setmealDao.setSetmealAndCheckGroup(map);
        }
    }
}
