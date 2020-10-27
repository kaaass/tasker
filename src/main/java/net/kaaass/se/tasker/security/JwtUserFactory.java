package net.kaaass.se.tasker.security;

import net.kaaass.se.tasker.dao.entity.UserEntity;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * 用户对象工厂
 */
public class JwtUserFactory {
    private JwtUserFactory() {
    }

    public static JwtUser create(UserEntity authEntity) {
        return new JwtUser(
                authEntity.getId(),
                authEntity.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(authEntity.getRoles()),
                authEntity.isEnable(),
                authEntity.isValidate()
        );
    }
}
