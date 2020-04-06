package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.OrderService;
import com.itheima.service.ReportService;
import com.itheima.service.SetmealService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 报表操作
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;
    @Reference
    private OrderService orderService;
    @Reference
    private ReportService reportService;

    //会员数量折线图数据
    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        Map<String,Object> map = new HashMap<>();
        List<String> months = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();//获得日历对象,默认时间就是当前时间
        //计算过去12个月
        calendar.add(Calendar.MONTH,-12);//获得当前时间往前推12个月的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
        for (int i = 0; i < 12 ; i ++){
            calendar.add(Calendar.MONTH,1);//获得当前时间往后推一个月日期
            Date date = calendar.getTime();
            months.add(simpleDateFormat.format(date));
        }
        map.put("months",months);

        List<Integer> memberCount = memberService.findMemberCountByMonths(months);
        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }

    //套餐预约数量占比
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        /*List<String> setmealNames = new ArrayList<>();
        List<Setmeal> setmealList = setmealService.findAll();
        List<Map> setmealCount = new ArrayList<>();
        for (Setmeal setmeal : setmealList){
            String setmealName = setmeal.getName();
            setmealNames.add(setmealName);
            Integer setmealId = setmeal.getId();
            Integer value = orderService.findCountBySetmealId(setmealId);
            Map<String,Object> map = new HashMap<>();
            map.put("name",setmealName);
            map.put("value",value);
            setmealCount.add(map);
        }
        Map<String,Object> data = new HashMap<>();
        data.put("setmealNames",setmealNames);
        data.put("setmealCount",setmealCount);
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,data);*/

        /*---------------------------------------------------------------------------*/

        List<Map<String,Object>> list = setmealService.findSetmealCount();

        Map<String,Object> map = new HashMap<>();
        map.put("setmealCount",list);

        List<String> setmealNames = new ArrayList<>();
        for (Map<String,Object> m : list){
            String name = (String)m.get("name");
            setmealNames.add(name);
        }
        map.put("setmealNames",setmealNames);
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
    }

    //获得运营统计数据
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        try {
            Map<String,Object> result = reportService.getBusinessReportData();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,result);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }

    }

    //导出运营数据
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try {
            //远程调用报表服务获取报表数据
            Map<String,Object> result = reportService.getBusinessReportData();

            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            String filePath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";
            //基于提供的Excel模板文件在内存中创建一个Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            //读取第一个工作表
            XSSFSheet sheet = excel.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for (Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            //使用输出流进行表格下载,基于浏览器作为客户端下载
            OutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");//代表的是Excel文件类型
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");//指定以附件形式进行下载
            excel.write(out);

            out.flush();
            out.close();
            excel.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    /*//导出运营数据到PDF文件并提供客户端下载
    @RequestMapping("/exportBusinessReport4PDF")
    public Result exportBusinessReport4PDF(HttpServletRequest request,HttpServletResponse response){
        try {
            Map<String,Object> result = reportService.getBusinessReportData();

            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //动态获取PDF模板文件绝对磁盘路径
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";
            String jasperPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";

            //编译模板
            JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);

            //填充数据
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath,result,new JRBeanCollectionDataSource(hotSetmeal));

            //创建输出流,用于服务器写数据到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");

            //输出文件
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);

            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/


    /*public static void main(String[] args) throws Exception {
        Date date = new  Date();
        System.out.println(DateUtils.parseDate2String(date));
    }*/

}
