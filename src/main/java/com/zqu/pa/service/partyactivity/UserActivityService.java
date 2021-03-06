package com.zqu.pa.service.partyactivity;

import java.util.List;
import java.util.Map;

import com.zqu.pa.vo.partyactivity.ActivityInfo;
import com.zqu.pa.vo.partyactivity.PageOfList;
import com.zqu.pa.vo.partyactivity.UserApplyInfo;

public interface UserActivityService {

    //根据页数和每页记录数，和所属党支部返回对应活动列表信息
    PageOfList getMenuInfo(int page, int num, int branchId);

    //点击我要报名后获取是否可以报名的信息
    Map getApplyResult(int activityId);

    //获取当前用户是否报过名
    int getIsApply(int activityId, String userId);

    //用户报名
    String applyActivity(Integer activityId, String phoneNum);

    //获取用户所有的报名信息
    List<UserApplyInfo> getUserApplyInfo(String userId);

    //撤销报名申请
    String deleteApply(Integer activityId, String userId);

    //获取通过审核人数
    int getActivityNum(int activityId);

    //获取相应活动详细信息
    ActivityInfo getActivityInfo(Integer activityId);
}
