package org.liuyehcf.mybatis;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by HCF on 2017/3/31.
 */
public interface CrmUserDAO {

    CrmUserDO selectById(Long anyName);

    int insert(CrmUserDO anyName);

    int update(CrmUserDO anyName);

    List<CrmUserDO> selectByFirstName(String anyName);

    List<CrmUserDO> selectByFirstNameAndLastName(String anyName1, String anyName2);

    CrmUserDO selectByIdWithParam(@Param("specificName") Long anyName);

    int insertWithParam(@Param("specificName") CrmUserDO anyName);

    int updateWithParam(@Param("specificName") CrmUserDO anyName);

    List<CrmUserDO> selectByFirstNameWithParam(@Param("specificName") String anyName);

    List<CrmUserDO> selectByFirstNameAndLastNameWithParam(@Param("specificName1") String anyName1, @Param("specificName2") String anyName2);

}