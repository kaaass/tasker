package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.ResourceEntity;
import net.kaaass.se.tasker.dao.entity.UserEntity;
import net.kaaass.se.tasker.dto.ResourceDto;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.vo.ResourceVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 资源相关数据结构映射器
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ResourceMapper {

    @Named("getUserUid")
    default String getUserUid(UserEntity userDto) {
        return userDto.getId();
    }

    ResourceVo dtoToVo(ResourceDto resourceDto);

    @Mapping(target = "uploaderUid", source = "uploader", qualifiedByName = "getUserUid")
    ResourceDto entityToDto(ResourceEntity resourceEntity);
}
