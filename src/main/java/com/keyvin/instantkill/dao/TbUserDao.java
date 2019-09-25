package com.keyvin.instantkill.dao;

import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author weiwh
 * @date 2019/8/11 12:17
 */
@Mapper
public interface TbUserDao {

    @Select("select * from tb_user where user_id=#{userId}")
    public TbUser getByUserId(@Param("userId") Long userId);

    @Insert("insert into tb_user(user_id,nickname,password,salt,head,register_date,last_login_date) " +
            "values(#{userId},#{nickname},#{password},#{salt},#{head},#{registerDate},#{lastLoginDate})")
    public int insert(TbUser user);

    @Select("select * from tb_user limit #{index},#{size}")
    public List<TbUser> getAllUsers(@Param("index")int index, @Param("size")int size);

    @Update("update tb_user set password = ${password} where id=${id}")
    public void update(TbUser newUser);
}
