package com.xinnuo.apple.nongda.entity;

/**
 * 管理员基类
 */


public class AdminModel {
    /**
     * birthday : null
     * phone : 123456
     * sex : 1
     * status : 2
     * jurisdiction : 1
     * collegeName : null
     * password : admin
     * id : 67
     * cardNo : null
     * picture : null
     * name : admin
     * itro : null
     * loginSta : success
     */

    private String birthday;
    private String phone;
    private int sex;
    private int status;
    private String jurisdiction;
    private String collegeName;
    private String password;
    private String id;
    private String cardNo;
    private String picture;
    private String name;
    private String itro;
    private String loginSta;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItro() {
        return itro;
    }

    public void setItro(String itro) {
        this.itro = itro;
    }

    public String getLoginSta() {
        return loginSta;
    }

    public void setLoginSta(String loginSta) {
        this.loginSta = loginSta;
    }
}
