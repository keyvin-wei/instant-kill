package com.keyvin.instantkill.service;

import com.keyvin.instantkill.dao.UserDao;
import com.keyvin.instantkill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

/**
 * @author weiwh
 * @date 2019/8/11 12:20
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);
    }

    //测试事务
    @Transactional
    public boolean tx(){
        User u1 = new User(1, "111");
        User u2 = new User(3, "3333");
        userDao.insert(u2);
        userDao.insert(u1);

        return true;

    }
}
