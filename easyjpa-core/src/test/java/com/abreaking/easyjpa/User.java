package com.abreaking.easyjpa;

import com.abreaking.easyjpa.mapper.annotation.Id;

import java.util.Date;

/**
 *
 * @author liwei_paas
 * @date 2020/11/13
 */
public class User {
    @Id
    private Integer userId;
    private Date birthday;
    private String userName;

    public User(Integer userId) {
        this.userId = userId;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public User(Integer userId, Date birthday, String userName) {
        this.userId = userId;
        this.birthday = birthday;
        this.userName = userName;
    }

    public User() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", birthday=" + birthday +
                ", userName='" + userName + '\'' +
                '}';
    }
}
