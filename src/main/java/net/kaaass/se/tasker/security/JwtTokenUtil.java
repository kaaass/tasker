package net.kaaass.se.tasker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.kaaass.se.tasker.dto.AuthTokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 校验 Jwt 权限令牌常用的方法
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.header}")
    private static String tokenHeader;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 从令牌获得用户名（即用户 uid）
     */
    public Optional<String> getUsernameFromToken(String token) {
        return getClaimsFromToken(token).map(Claims::getSubject);
    }

    /**
     * 从令牌获得令牌签发日期
     */
    public Optional<Date> getCreatedDateFromToken(String token) {
        return getClaimsFromToken(token).map(claims -> new Date((long) claims.get(CLAIM_KEY_CREATED)));
    }

    /**
     * 从令牌获得令牌过期日期
     */
    public Optional<Date> getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).map(Claims::getExpiration);
    }

    /**
     * 从 jwt 令牌获得 claims 段
     */
    private Optional<Claims> getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.of(claims);
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 判断令牌是否过期
     */
    private boolean isTokenExpired(String token) {
        Optional<Date> expiration = getExpirationDateFromToken(token);
        return expiration.map(time -> time.before(new Date())).orElse(true);
    }

    /**
     * 从用户详细信息产生鉴权对象
     */
    public AuthTokenDto generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从 claims 产生鉴权对象
     */
    private AuthTokenDto generateToken(Map<String, Object> claims) {
        Date expiration = generateExpirationDate();
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return new AuthTokenDto(token, expiration);
    }

    /**
     * 判断令牌可否刷新
     */
    public boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新令牌
     */
    public Optional<AuthTokenDto> refreshToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> {
                    claims.put(CLAIM_KEY_CREATED, new Date());
                    return claims;
                })
                .map(this::generateToken);
    }

    /**
     * 校验令牌是否合法
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        var user = (JwtUser) userDetails;
        return getUsernameFromToken(token)
                .map(username -> username.equals(user.getUsername()) && !isTokenExpired(token))
                .orElse(false);
    }

    /**
     * 校验令牌是否合法
     */
    public boolean validateToken(String token) {
        var username = getUsernameFromToken(token);
        if (username.isEmpty())
            return false;
        var userDetails = this.userDetailsService.loadUserByUsername(username.get());
        return validateToken(token, userDetails);
    }

    /**
     * 从请求中获得令牌
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(tokenHeader);
    }

    /**
     * 加密原始密码
     */
    public String encryptPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(rawPassword);
    }
}

