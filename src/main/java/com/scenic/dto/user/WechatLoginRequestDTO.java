package com.scenic.dto.user;

import java.io.Serializable;

/**
 * 微信登录请求DTO
 */
public class WechatLoginRequestDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 微信登录临时凭证
     */
    private String code;
    
    /**
     * 用户信息：昵称
     */
    private String nickName;
    
    /**
     * 用户信息：头像URL
     */
    private String avatarUrl;
    
    /**
     * 用户信息：性别
     */
    private Integer gender;
    
    /**
     * 用户信息：国家
     */
    private String country;
    
    /**
     * 用户信息：省份
     */
    private String province;
    
    /**
     * 用户信息：城市
     */
    private String city;
    
    /**
     * 用户信息：语言
     */
    private String language;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
