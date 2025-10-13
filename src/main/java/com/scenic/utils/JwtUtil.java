package com.scenic.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.scenic.config.JwtConfig;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * JWT工具类，用于生成和验证JWT令牌
 */
@Component
public class JwtUtil {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    // Redis中存储token的key前缀
    private static final String ADMIN_TOKEN_KEY_PREFIX = "admin:token:";
    private static final String MINIAPP_TOKEN_KEY_PREFIX = "miniapp:token:";
    
    // 生成管理员JWT令牌
    public String generateAdminToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("role", "admin");
        
        // 生成JWT令牌
        String token = createToken(claims, username, jwtConfig.getAdminSecret(), jwtConfig.getAdminExpiration());
        
        // 生成唯一的tokenId
        String tokenId = UUID.randomUUID().toString();
        
        // 将token存储到Redis中
        String redisKey = ADMIN_TOKEN_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(redisKey, token, jwtConfig.getAdminExpiration(), TimeUnit.MILLISECONDS);
        
        return token;
    }
    
    // 生成小程序用户JWT令牌
    public String generateMiniappToken(String openId, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("openId", openId);
        claims.put("userId", userId);
        claims.put("role", "user");
        
        // 生成JWT令牌
        String token = createToken(claims, openId, jwtConfig.getMiniappSecret(), jwtConfig.getMiniappExpiration());
        
        // 生成唯一的tokenId
        String tokenId = UUID.randomUUID().toString();
        
        // 将token存储到Redis中
        String redisKey = MINIAPP_TOKEN_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(redisKey, token, jwtConfig.getMiniappExpiration(), TimeUnit.MILLISECONDS);
        
        return token;
    }
    
    // 验证管理员JWT令牌
    public boolean validateAdminToken(String token) {
        try {
            // 从JWT令牌中获取用户ID
            Claims claims = getAllClaimsFromToken(token, jwtConfig.getAdminSecret());
            Long userId = claims.get("userId", Long.class);
            
            // 从Redis中获取token
            String redisKey = ADMIN_TOKEN_KEY_PREFIX + userId;
            String storedToken = redisTemplate.opsForValue().get(redisKey);
            
            // 验证token是否存在于Redis中，并且与传入的token一致
            if (storedToken == null || !storedToken.equals(token)) {
                return false;
            }
            
            // 验证token是否过期
            return !isTokenExpired(token, jwtConfig.getAdminSecret());
        } catch (Exception e) {
            return false;
        }
    }
    
    // 验证小程序用户JWT令牌
    public boolean validateMiniappToken(String token) {
        try {
            // 从JWT令牌中获取用户ID
            Claims claims = getAllClaimsFromToken(token, jwtConfig.getMiniappSecret());
            Long userId = claims.get("userId", Long.class);
            
            // 从Redis中获取token
            String redisKey = MINIAPP_TOKEN_KEY_PREFIX + userId;
            String storedToken = redisTemplate.opsForValue().get(redisKey);
            
            // 验证token是否存在于Redis中，并且与传入的token一致
            if (storedToken == null || !storedToken.equals(token)) {
                return false;
            }
            
            // 验证token是否过期
            return !isTokenExpired(token, jwtConfig.getMiniappSecret());
        } catch (Exception e) {
            return false;
        }
    }
    
    // 使管理员JWT令牌失效
    public void invalidateAdminToken(Long userId) {
        String redisKey = ADMIN_TOKEN_KEY_PREFIX + userId;
        redisTemplate.delete(redisKey);
    }
    
    // 使小程序用户JWT令牌失效
    public void invalidateMiniappToken(Long userId) {
        String redisKey = MINIAPP_TOKEN_KEY_PREFIX + userId;
        redisTemplate.delete(redisKey);
    }
    
    // 从JWT令牌中获取用户名
    public String getUsernameFromToken(String token, String secret) {
        return getClaimFromToken(token, Claims::getSubject, secret);
    }
    
    // 从JWT令牌中获取过期时间
    public Date getExpirationDateFromToken(String token, String secret) {
        return getClaimFromToken(token, Claims::getExpiration, secret);
    }
    
    // 从JWT令牌中获取指定声明
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, String secret) {
        final Claims claims = getAllClaimsFromToken(token, secret);
        return claimsResolver.apply(claims);
    }
    
    // 从JWT令牌中获取所有声明
    private Claims getAllClaimsFromToken(String token, String secret) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // 检查JWT令牌是否过期
    private Boolean isTokenExpired(String token, String secret) {
        final Date expiration = getExpirationDateFromToken(token, secret);
        return expiration.before(new Date());
    }
    
    // 创建JWT令牌
    private String createToken(Map<String, Object> claims, String subject, String secret, long expiration) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
