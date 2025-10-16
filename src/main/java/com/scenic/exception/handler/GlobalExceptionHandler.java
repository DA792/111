package com.scenic.exception.handler;

import com.scenic.common.dto.Result;
import com.scenic.exception.reservation.DuplicateReservationException;
import com.scenic.exception.reservation.ReservationException;
import com.scenic.exception.reservation.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理预约重复异常
     */
    @ExceptionHandler(DuplicateReservationException.class)
    public ResponseEntity<Result<?>> handleDuplicate(DuplicateReservationException e) {
        log.warn("重复预约异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
               .body(Result.error(409, e.getMessage()));
    }
    
    /**
     * 处理服务不可用异常
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Result<?>> handleServiceUnavailable(ServiceUnavailableException e) {
        log.error("服务不可用异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
               .body(Result.error(503, e.getMessage()));
    }
    
    /**
     * 处理预约业务异常
     */
    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<Result<?>> handleReservationException(ReservationException e) {
        log.error("预约业务异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(Result.error(e.getCode(), e.getMessage()));
    }
    
    /**
     * 处理Redis连接异常
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<Result<?>> handleRedisConnectionFailure(RedisConnectionFailureException e) {
        log.error("Redis连接失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
               .body(Result.error(503, "缓存服务暂时不可用"));
    }
    
    /**
     * 处理数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<?>> handleDataAccessException(DataAccessException e) {
        log.error("数据库访问异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(Result.error(500, "数据访问异常"));
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(Result.error(500, "系统内部错误"));
    }
}