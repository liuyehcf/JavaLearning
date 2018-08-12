package org.liuyehcf.spring.tx

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.SelectKey
import org.apache.ibatis.mapping.StatementType

/**
 * @author hechenfeng
 * @date 2018/8/11
 */
@Mapper
interface UserDAO {
    @Insert('''
        INSERT INTO user(
        name,
        age
        )VALUES(
        #{name},
        #{age}
        )
    ''')
    @SelectKey(before = false, keyProperty = "id", resultType = Long.class, statementType = StatementType.STATEMENT, statement = "SELECT LAST_INSERT_ID()")
    int insert(UserDO userDO)
}