package com.scenic.exception.reservation;

/**
 * 预约业务异常基类
 */
public class ReservationException extends RuntimeException {
    private int code;
    private String message;

    public ReservationException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ReservationException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}