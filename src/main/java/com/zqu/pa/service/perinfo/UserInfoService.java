package com.zqu.pa.service.perinfo;

import java.util.List;

import com.zqu.pa.common.ServerResponse;
import com.zqu.pa.entity.perinfo.UserPartyInfo;
import com.zqu.pa.entity.perinfo.UserPersonInfo;
import com.zqu.pa.vo.perinfo.AllUserInfo;
import com.zqu.pa.vo.perinfo.Branch;
import com.zqu.pa.vo.perinfo.GradeClassSortList;
import com.zqu.pa.vo.perinfo.Role;
import com.zqu.pa.vo.perinfo.UserCheckList;
import com.zqu.pa.vo.perinfo.UserList;
import com.zqu.pa.vo.perinfo.UserListInfo;

public interface UserInfoService {

    //根据id搜索该用户个人信息
    UserPersonInfo getUserPersonInfo(String userId);
    
    //根据id搜索该用户党员信息
    UserPartyInfo getUserPartyInfo(String userId);
    
    //用户根据userId来修改个人信息
    int updateByUserPerson(UserPersonInfo info);

    //用户根据userId来修改党员信息
    int updateByUserParty(UserPartyInfo info);

    //根据所属党支部返回用户信息列表，checkState为1未审核通过的
    //branchId==0表示查询所有党支部人员信息
    List<UserList> getUserList(int branchId, int checkState);

    //根据所属党支部返回用户审核信息列表
    List<UserCheckList> getUserCheckList(int branchId);
/*    
    //验证所属党支部是否相同，再根据其userId审核该用户
    String checkUser(int branchId, String userId);
*/
    //根据所属党支部，返回选择年级和班级列表
    GradeClassSortList getGradeClass(int branchId);

    //批量审核(userId格式为userId1&userId2..)：验证所属党支部是否相同，再根据其userId审核用户
    String checkUserByBatch(int branchId, String userId);

    //批量删除(userId格式为userId1&userId2..)：验证所属党支部是否相同，再根据其userId删除用户
    String deleteUser(int branchId, String userId);

    //创建未审核的新用户
    String insertNewUser(AllUserInfo user);
    
    //返回党支部列表
    List<Branch> getBranchList();
    
    //返回角色列表
    List<Role> getRoleList();

    //更新头像
    int updateImgHead(String fullPath, String userId);

    //修改密码
    ServerResponse updatePassword(String userId, String old_password, String new_password);
}
