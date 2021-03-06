package com.abreaking.easyjpa;

import javax.persistence.Id;
import java.util.Date;

/**
 *
 * @author liwei_paas
 * @date 2020/11/13
 */
public class User {
    @Id
    private Integer userId;
    private String userName;
    private Date birthday;
    private Long phoneNo;
    private Float height;

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

    public User(String userName,Date birthday) {
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

    public Long getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(Long phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", birthday=" + birthday +
                ", phoneNo=" + phoneNo +
                ", height=" + height +
                '}';
    }
}
