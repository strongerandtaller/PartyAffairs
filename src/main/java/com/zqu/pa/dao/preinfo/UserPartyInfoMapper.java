package com.zqu.pa.dao.preinfo;

import com.zqu.pa.entity.preinfo.UserPartyInfo;
import com.zqu.pa.entity.preinfo.UserPartyInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserPartyInfoMapper {
    long countByExample(UserPartyInfoExample example);

    int deleteByExample(UserPartyInfoExample example);

    int deleteByPrimaryKey(String userId);

    int insert(UserPartyInfo record);

    int insertSelective(UserPartyInfo record);

    List<UserPartyInfo> selectByExample(UserPartyInfoExample example);

    UserPartyInfo selectByPrimaryKey(String userId);

    int updateByExampleSelective(@Param("record") UserPartyInfo record, @Param("example") UserPartyInfoExample example);

    int updateByExample(@Param("record") UserPartyInfo record, @Param("example") UserPartyInfoExample example);

    int updateByPrimaryKeySelective(UserPartyInfo record);

    int updateByPrimaryKey(UserPartyInfo record);
}