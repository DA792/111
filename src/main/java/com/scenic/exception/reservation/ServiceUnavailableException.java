package com.scenic.exception.reservation;

/**
 * 服务不可用异常
 */
public class ServiceUnavailableException extends ReservationException {
    
    public ServiceUnavailableException(String message) {
        super(503, message);
    }
    
    public ServiceUnavailableException() {
        super(503, "服务暂时不可用");
    }
}