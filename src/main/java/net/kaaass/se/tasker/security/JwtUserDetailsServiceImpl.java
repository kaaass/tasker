package net.kaaass.se.tasker.security;

import net.kaaass.se.tasker.dao.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 实现用户详细信息的请求与查找
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserAuthRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var result = repository.findById(username);
        var authEntity = result
                .orElseThrow(() -> new UsernameNotFoundException(String.format("No user found with id '%s'.", username)));
        return JwtUserFactory.create(authEntity);
    }
}
