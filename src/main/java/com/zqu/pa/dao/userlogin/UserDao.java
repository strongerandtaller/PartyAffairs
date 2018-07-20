package com.zqu.pa.dao.userlogin;

import java.util.List;

import com.zqu.pa.entity.userlogin.User;

public interface UserDao {

    User getUser();

    User getUserLogin(String userId);
    
    /**
     * 根据身份ID获取权限列表
     * @param roleId
     * @return
     */
    List<String> getRolePermission(int roleId);

    String getRealName(String userId);
}
