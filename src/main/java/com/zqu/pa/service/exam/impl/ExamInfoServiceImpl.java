package com.zqu.pa.service.exam.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zqu.pa.common.ServerResponse;
import com.zqu.pa.dao.exam.ExamInfoMapper;
import com.zqu.pa.dao.exam.ExamInfoReviewMapper;
import com.zqu.pa.dao.exam.ExamUserMapper;
import com.zqu.pa.dao.perinfo.UserPersonInfoMapper;
import com.zqu.pa.entity.exam.ExamInfo;
import com.zqu.pa.entity.exam.ExamInfoExample;
import com.zqu.pa.entity.exam.ExamInfoReview;
import com.zqu.pa.entity.exam.ExamInfoReviewExample;
import com.zqu.pa.entity.exam.ExamUserExample;
import com.zqu.pa.entity.exam.ExamUserKey;
import com.zqu.pa.entity.perinfo.UserPersonInfo;
import com.zqu.pa.entity.perinfo.UserPersonInfoExample;
import com.zqu.pa.service.exam.ExamInfoService;
import com.zqu.pa.utils.DateUtil;
import com.zqu.pa.vo.exam.AdminExamInfoList;
import com.zqu.pa.vo.exam.ResponseNowExamList;
import com.zqu.pa.vo.userInfo.UserBasicInfo;

@Service
public class ExamInfoServiceImpl implements ExamInfoService {

    private Logger logger = LoggerFactory.getLogger(ExamInfoServiceImpl.class);
    
    @Autowired
    private ExamInfoMapper examInfoMapper;
    
    @Autowired
    private ExamInfoReviewMapper examInfoReviewMapper;
    
    @Autowired
    private ExamUserMapper examUserMapper;
    
    @Autowired
    private UserPersonInfoMapper userPersonInfoMapper;
    
    @Transactional
    @Override
    public ServerResponse createExamInfo(ExamInfo examInfo, ExamInfoReview examInfoReview) {
        
        UserBasicInfo basicInfo = (UserBasicInfo)SecurityUtils.getSubject().getSession().getAttribute("basicInfo");
        if (basicInfo == null) {
            logger.debug("用户未登录");
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userId = basicInfo.getUserId();
        int branchId = basicInfo.getBranchId();
        int roleId = basicInfo.getRoleId();
        
        examInfo.setBranchId(branchId);
        int i = examInfoMapper.insertExamInfo(examInfo);
        if (i <= 0)
            return ServerResponse.createByError();
        Integer examId = examInfo.getExamId();
        examInfoReview.setExamId(examId);
        examInfoReview.setCreateId(userId);
        i = examInfoReviewMapper.insertSelective(examInfoReview);
        if (i <= 0)
            return ServerResponse.createByError();
        
        return ServerResponse.createBySuccess();
    }

    @Override
    public List<AdminExamInfoList> unreviewExamInfo() {
        
        ExamInfoReviewExample example = new ExamInfoReviewExample();
        //未审核
        int review = 0;
        example.createCriteria().andReviewEqualTo(review);
        List<ExamInfoReview> listExamInfoReview = examInfoReviewMapper.selectByExample(example);
        if (listExamInfoReview == null || listExamInfoReview.size() == 0) {
            logger.debug("未审核的考试为空");
            //返回空数组
            return new ArrayList<>();
        }
        List<AdminExamInfoList> listResult = new ArrayList<>(); 
        for (ExamInfoReview e : listExamInfoReview) {
            AdminExamInfoList unreviewList = new AdminExamInfoList();
            Integer examId = e.getExamId();
            //考试基本信息
            ExamInfo examInfo = examInfoMapper.selectByPrimaryKey(examId);
            unreviewList.setExamId(examId);
            unreviewList.setExamTitle(examInfo.getExamTitle());
            unreviewList.setStartTime(DateUtil.formatTime(examInfo.getStartTime()));
            unreviewList.setEndTime(DateUtil.formatTime(examInfo.getEndTime()));
            unreviewList.setExamPeriod(examInfo.getExamPeriod());
            unreviewList.setStatus(examInfo.getFinish());
            unreviewList.setSingleQuantity(examInfo.getSingleQuantity());
            unreviewList.setMultipleQuantity(examInfo.getMultipleQuantity());
            unreviewList.setPassScore(examInfo.getPassScore());
            unreviewList.setBranchId(examInfo.getBranchId());
            //TODO 根据党支部ID获取党支部名称
            //unreviewList.setBranchName(branchName);
            
            unreviewList.setSingleScore(e.getSingleScore());
            unreviewList.setMultipleScore(e.getMultipleScore());
            unreviewList.setCreateId(e.getCreateId());
            UserPersonInfo userPersonInfo = userPersonInfoMapper.selectByPrimaryKey(e.getCreateId());
            if (userPersonInfo != null)
                unreviewList.setCreateName(userPersonInfo.getName());
            
            //考试参与人员ID数组
            List<String> listExamUserID = new ArrayList<>();
            ExamUserExample example1 = new ExamUserExample();
            example1.createCriteria().andExamIdEqualTo(examId);
            List<ExamUserKey> listExamUser = examUserMapper.selectByExample(example1);
            for (ExamUserKey euk : listExamUser) {
                listExamUserID.add(euk.getUserId());
            }
            unreviewList.setListUserID(listExamUserID);
            
            if (listExamUserID != null && listExamUserID.size() != 0) {
                //根据listExamUserID查找用户名字组成数组
                List<String> listExamUserName = new ArrayList<>();
                UserPersonInfoExample example2 = new UserPersonInfoExample();
                example2.createCriteria().andUserIdIn(listExamUserID);
                List<UserPersonInfo> listUserPerson = userPersonInfoMapper.selectByExample(example2);
                for (UserPersonInfo upi : listUserPerson) {
                    listExamUserName.add(upi.getName());
                }
                unreviewList.setListUserName(listExamUserName);
            }

            listResult.add(unreviewList);
        }
        
        
        return listResult;
    }

    @Override
    public ServerResponse reviewExamInfo(Integer examId) {
        
        UserBasicInfo basicInfo = (UserBasicInfo)SecurityUtils.getSubject().getSession().getAttribute("basicInfo");
        if (basicInfo == null) {
            logger.debug("用户未登录");
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        String userId = basicInfo.getUserId();
        ExamInfoReview examInfoReview = new ExamInfoReview();
        examInfoReview.setExamId(examId);
        examInfoReview.setReviewId(userId);
        examInfoReview.setReview(1);
        int i = examInfoReviewMapper.updateByPrimaryKeySelective(examInfoReview);
        if (i > 0)
            return ServerResponse.createBySuccess();
        return ServerResponse.createByError();
    }

}
