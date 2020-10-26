package net.kaaass.se.tasker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

/**
 * 用户相关数据结构变换类
 */
@Mapper(componentModel = "spring")
public class UserTransform {

    @Named("mapRoles")
    public List<String> mapRoles(String roles) {
        return Arrays.asList(roles.split(","));
    }
}
