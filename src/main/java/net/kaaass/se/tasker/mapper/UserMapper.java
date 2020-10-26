package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.UserAuthEntity;
import net.kaaass.se.tasker.dto.UserAuthDto;
import net.kaaass.se.tasker.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserVo userAuthDtoToVo(UserAuthDto authDto);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    UserAuthDto userAuthEntityToDto(UserAuthEntity authEntity);

    @Named("mapRoles")
    default List<String> mapRoles(String roles) {
        return Arrays.asList(roles.split(","));
    }
}
