package org.liuyehcf.dao;

import org.liuyehcf.entity.CrmUser;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

/**
 * Created by HCF on 2017/3/31.
 */
public interface CrmUserDAO {

    @Select({
            "SELECT id, first_name, last_name, age, sex FROM crm_user " +
                    "WHERE id = #{id, jdbcType=BIGINT}"
    })
    @Results({
            @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
            @Result(column = "first_name", property = "firstName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "last_name", property = "lastName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "age", property = "age", jdbcType = JdbcType.SMALLINT),
            @Result(column = "sex", property = "sex", jdbcType = JdbcType.TINYINT),
    })
    CrmUser selectCrmUserById(@Param("id") Long id);

    @Insert({
            "INSERT INTO crm_user(first_name, last_name, age, sex) " +
                    "VALUES(#{crmUser.firstName, jdbcType=VARCHAR}, #{crmUser.lastName, jdbcType=VARCHAR}, #{crmUser.age, jdbcType=SMALLINT}, #{crmUser.sex, jdbcType=TINYINT})"

    })
    int insertCrmUser(@Param("crmUser") CrmUser crmUser);


    @Update({
            "<script>",
            "UPDATE crm_user SET " +

                    "<if test=\" crmUser.firstName != null and crmUser.firstName !='' \"> first_name = #{crmUser.firstName, jdbcType=VARCHAR}, </if> " +
                    "<if test=\" crmUser.lastName != null and crmUser.lastName !='' \"> last_name = #{crmUser.lastName, jdbcType=VARCHAR}, </if> " +
                    "<if test=\" crmUser.age != null \"> age = #{crmUser.age, jdbcType=SMALLINT}, </if> " +
                    "<if test=\" crmUser.sex != null \"> sex= #{crmUser.sex, jdbcType=TINYINT} </if> " +
                    "WHERE id = #{crmUser.id, jdbcType=BIGINT} "
            , "</script>"
    })
    int updateCrmUser(@Param("crmUser") CrmUser crmUser);
}