package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.ResourceEntity;
import net.kaaass.se.tasker.dto.ResourceDto;
import net.kaaass.se.tasker.vo.ResourceVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 资源相关数据结构映射器
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ResourceMapper {

    ResourceVo dtoToVo(ResourceDto resourceDto);

    ResourceDto entityToDto(ResourceEntity resourceEntity);
}
