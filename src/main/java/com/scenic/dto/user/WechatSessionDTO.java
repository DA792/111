package com.scenic.dto.user;

import java.io.Serializable;

/**
 * 微信会话信息DTO
 */
public class WechatSessionDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户唯一标识
     */
    private String openid;
    
    /**
     * 会话密钥
     */
    private String sessionKey;
    
    /**
     * 用户在开放平台的唯一标识符
     */
    private String unionid;
    
    /**
     * 错误码
     */
    private Integer errcode;
    
    /**
     * 错误信息
     */
    private String errmsg;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
    
    /**
     * 判断是否请求成功
     */
    public boolean isSuccess() {
        return errcode == null || errcode == 0;
    }
}
