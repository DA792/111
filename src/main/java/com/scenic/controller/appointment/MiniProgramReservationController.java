package com.scenic.controller.appointment;

import com.scenic.common.dto.PageResult;
import com.scenic.common.dto.Result;
import com.scenic.entity.appointment.IndividualReservation;
import com.scenic.service.appointment.IndividualReservationService;
import com.scenic.utils.JwtUtil;
import com.scenic.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 小程序端预约控制器
 * 提供小程序端的API接口
 */
@RestController
@RequestMapping("/api/uniapp/individual-reservations")
public class MiniProgramReservationController {
    
    @Autowired
    private IndividualReservationService individualReservationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisUtil redisUtil;
    
    // Redis中存储核销token的key前缀
    private static final String VERIFICATION_TOKEN_KEY_PREFIX = "verification:token:";
    // 核销token过期时间（24小时）
    private static final long VERIFICATION_TOKEN_EXPIRATION = 24 * 60 * 60;
    
    /**
     * 根据证件号码和预约状态查询个人预约（分页）
     * @param idNumber 证件号码
     * @param status 预约状态（可选）
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 个人预约列表
     */
    @GetMapping
    public Result<PageResult<IndividualReservation>> getReservationsByIdNumber(
            @RequestParam(required = false) String idNumber,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) java.util.Map<String, Object> params,
            @RequestParam(value = "params[idNumber]", required = false) String paramsIdNumber,
            @RequestParam(value = "params[status]", required = false) Integer paramsStatus,
            @RequestParam(value = "params[page]", required = false) Integer paramsPage,
            @RequestParam(value = "params[size]", required = false) Integer paramsSize) {
        
        try {
            // 打印所有参数，用于调试
            System.out.println("接收到的参数：");
            System.out.println("idNumber: " + idNumber);
            System.out.println("status: " + status);
            System.out.println("page: " + page);
            System.out.println("size: " + size);
            System.out.println("params: " + params);
            System.out.println("paramsIdNumber: " + paramsIdNumber);
            System.out.println("paramsStatus: " + paramsStatus);
            System.out.println("paramsPage: " + paramsPage);
            System.out.println("paramsSize: " + paramsSize);
            
            // 优先使用直接传递的参数
            if (paramsIdNumber != null) {
                idNumber = paramsIdNumber;
            }
            if (paramsStatus != null) {
                status = paramsStatus;
            }
            if (paramsPage != null) {
                page = paramsPage;
            }
            if (paramsSize != null) {
                size = paramsSize;
            }
            
            // 处理小程序传递的params对象格式
            if (params != null) {
                System.out.println("params内容: " + params);
                if (idNumber == null && params.containsKey("idNumber")) {
                    idNumber = params.get("idNumber").toString();
                    System.out.println("从params中获取idNumber: " + idNumber);
                }
                if (status == null && params.containsKey("status")) {
                    status = Integer.parseInt(params.get("status").toString());
                    System.out.println("从params中获取status: " + status);
                }
                if (params.containsKey("page")) {
                    page = Integer.parseInt(params.get("page").toString());
                    System.out.println("从params中获取page: " + page);
                }
                if (params.containsKey("size")) {
                    size = Integer.parseInt(params.get("size").toString());
                    System.out.println("从params中获取size: " + size);
                }
            }
            
            System.out.println("最终使用的参数：");
            System.out.println("idNumber: " + idNumber);
            System.out.println("status: " + status);
            System.out.println("page: " + page);
            System.out.println("size: " + size);
            
            // 为了测试，直接使用硬编码的证件号码
            idNumber = "520114200401240016";
            System.out.println("硬编码测试：使用证件号码 " + idNumber);
            
            if (idNumber == null || idNumber.isEmpty()) {
                return Result.error("证件号码不能为空");
            }
            
            Result<PageResult<IndividualReservation>> result = individualReservationService.getReservationsByIdNumber(idNumber, status, page, size);
            System.out.println("查询结果：" + result);
            
            // 检查结果是否为空
            if (result.getCode() == 200 && result.getData() != null) {
                PageResult<IndividualReservation> pageResult = result.getData();
                System.out.println("总记录数：" + pageResult.getTotal());
                System.out.println("当前页记录数：" + (pageResult.getRecords() != null ? pageResult.getRecords().size() : 0));
            }
            
            return result;
        } catch (Exception e) {
            return Result.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID查询预约详情（小程序端）
     * 如果是待核销状态，会生成token用于二维码
     * @param id 预约ID
     * @return 预约详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getReservationDetail(@PathVariable Long id) {
        try {
            // 查询预约详情
            Result<IndividualReservation> result = individualReservationService.getReservationById(id);
            
            if (result.getCode() != 200 || result.getData() == null) {
                return Result.error("预约记录不存在");
            }
            
            IndividualReservation reservation = result.getData();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("reservation", reservation);
            
            // 如果是待核销状态（status=0），生成token
            if (reservation.getStatus() != null && reservation.getStatus() == 0) {
                // 生成唯一的tokenId
                String tokenId = UUID.randomUUID().toString();
                
                // 构建token数据
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put("reservationId", reservation.getId());
                tokenData.put("reservationNo", reservation.getReservationNo());
                tokenData.put("visitDate", reservation.getVisitDate());
                tokenData.put("timeSlot", reservation.getTimeSlot());
                tokenData.put("contactName", reservation.getContactName());
                tokenData.put("contactIdNumber", reservation.getContactIdNumber());
                tokenData.put("totalCount", reservation.getTotalCount());
                tokenData.put("tokenId", tokenId);
                
                // 生成JWT令牌
                String token = jwtUtil.generateMiniappToken(tokenId, reservation.getUserId());
                
                // 将token数据存入Redis，用于核销时验证
                String redisKey = VERIFICATION_TOKEN_KEY_PREFIX + tokenId;
                redisUtil.set(redisKey, tokenData, VERIFICATION_TOKEN_EXPIRATION);
                
                // 将token返回给前端
                responseData.put("verificationToken", token);
                responseData.put("tokenId", tokenId);
            }
            
            return Result.success("查询成功", responseData);
        } catch (Exception e) {
            return Result.error("查询异常：" + e.getMessage());
        }
    }
    
    /**
     * 取消个人预约（小程序端）
     * @param id 预约ID
     * @param cancelReason 取消原因
     * @return 取消结果
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelReservation(@PathVariable Long id,
                                          @RequestParam(required = false) String cancelReason,
                                          @RequestParam(required = false) java.util.Map<String, Object> params) {
        try {
            // 处理小程序传递的params对象格式
            if (params != null && cancelReason == null && params.containsKey("cancelReason")) {
                cancelReason = params.get("cancelReason").toString();
            }
            
            if (cancelReason == null || cancelReason.isEmpty()) {
                cancelReason = "用户取消预约";
            }
            
            // 小程序端取消预约，使用默认的更新人
            return individualReservationService.cancelReservation(id, cancelReason, 0L);
        } catch (Exception e) {
            return Result.error("取消预约异常：" + e.getMessage());
        }
    }
    
    /**
     * 验证核销token
     * @param tokenId token ID
     * @param token JWT token
     * @return 验证结果
     */
    @GetMapping("/verify-token")
    public Result<Map<String, Object>> verifyToken(
            @RequestParam String tokenId,
            @RequestParam String token) {
        try {
            // 验证token是否有效
            if (!jwtUtil.validateMiniappToken(token)) {
                return Result.error("无效的核销码");
            }
            
            // 从Redis中获取token数据
            String redisKey = VERIFICATION_TOKEN_KEY_PREFIX + tokenId;
            Object tokenDataObj = redisUtil.get(redisKey);
            
            if (tokenDataObj == null) {
                return Result.error("核销码已过期或不存在");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenData = (Map<String, Object>) tokenDataObj;
            
            // 获取预约ID
            Long reservationId = ((Number) tokenData.get("reservationId")).longValue();
            
            // 查询预约详情
            Result<IndividualReservation> result = individualReservationService.getReservationById(reservationId);
            
            if (result.getCode() != 200 || result.getData() == null) {
                return Result.error("预约记录不存在");
            }
            
            IndividualReservation reservation = result.getData();
            
            // 检查预约状态
            if (reservation.getStatus() != null && reservation.getStatus() != 0) {
                if (reservation.getStatus() == 1) {
                    return Result.error("预约已取消");
                } else if (reservation.getStatus() == 10) {
                    return Result.error("预约已核销");
                } else {
                    return Result.error("预约状态异常");
                }
            }
            
            // 返回预约信息
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("reservation", reservation);
            responseData.put("tokenData", tokenData);
            
            return Result.success("验证成功", responseData);
        } catch (Exception e) {
            return Result.error("验证异常：" + e.getMessage());
        }
    }
}