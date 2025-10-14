package com.scenic.exception.reservation;

/**
 * 重复预约异常
 */
public class DuplicateReservationException extends ReservationException {
    
    public DuplicateReservationException(String message) {
        super(409, message);
    }
    
    public DuplicateReservationException() {
        super(409, "同一时段已存在预约");
    }
}