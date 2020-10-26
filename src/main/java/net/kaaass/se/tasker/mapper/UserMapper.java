package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.UserAuthEntity;
import net.kaaass.se.tasker.dto.UserAuthDto;
import net.kaaass.se.tasker.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 用户相关数据结构映射器
 */
@Mapper(componentModel = "spring", uses = {UserTransform.class})
public interface UserMapper {

    UserVo userAuthDtoToVo(UserAuthDto authDto);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    UserAuthDto userAuthEntityToDto(UserAuthEntity authEntity);
}
