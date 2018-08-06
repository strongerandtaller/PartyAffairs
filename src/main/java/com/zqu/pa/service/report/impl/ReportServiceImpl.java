package com.zqu.pa.service.report.impl;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zqu.pa.dao.report.ReportMapper;
import com.zqu.pa.entity.report.Report;
import com.zqu.pa.entity.report.ReportExample;
import com.zqu.pa.entity.report.ReportExample.Criteria;
import com.zqu.pa.service.report.ReportService;
import com.zqu.pa.vo.report.PageOfReport;

@Service("reportService")
public class ReportServiceImpl implements ReportService {
	
	@Autowired
	ReportMapper reportMapper;
	
	@Override
	public String InsertReport(Report report) {
		 //获取当前用户userId
        String userId = (String)SecurityUtils.getSubject().getSession().getAttribute("userId");
        if(userId==null)
            return "无法获取当前session信息";
        
        //设置思想报告人
        report.setUserId(userId);
        
        
        //数据库插入新闻
        int result = reportMapper.insert(report);
   
        if(result==0)
            return "提交思想报告失败";
        return "提交思想报告成功";
	}

	@Override
	public PageOfReport getPageOfReport(int page, int num) {
		String userId = null;
		try{
			//获取当前用户userId
			userId = (String)SecurityUtils.getSubject().getSession().getAttribute("userId");
			
		}catch (Exception e) {
			
			System.out.println("userId is null");
			
		}finally{
			
			if(userId==null){
				return null;
			}
		}
		
        
	            
		PageOfReport pageOfReport = new PageOfReport();
		
		
		ReportExample reportExample = new ReportExample();
		Criteria criteria = reportExample.createCriteria();
		criteria.andUserIdEqualTo(userId);
		 //总记录条数
        int totalInfoNum = (int) reportMapper.countByExample(reportExample);
        pageOfReport.setTotalInfoNum(totalInfoNum);
        //总页数
        int totalPageNum = (int)(totalInfoNum+num-1)/num;
        pageOfReport.setTotalPageNum(totalPageNum);
        
        if(totalPageNum<page)
            page = totalPageNum;
        pageOfReport.setPageNum(page);
        
        //limit index,num  从第index+1条记录开始，num条记录
        int index = (page-1)*num;
        pageOfReport.setList(reportMapper.getPageOfReportListLimited(userId,index, num));
        
        return pageOfReport;
	}

	@Override
	public Report getReportInfo(int report_id) {
		String userId = null;
		try{
			//获取当前用户userId
			userId = (String)SecurityUtils.getSubject().getSession().getAttribute("userId");
			
		}catch (Exception e) {
			
			System.out.println("userId is null");
			
		}finally{
			
			if(userId==null){
				return null;
			}
		}
		
		
		return reportMapper.getReportInfo(userId,report_id);
	}

}
