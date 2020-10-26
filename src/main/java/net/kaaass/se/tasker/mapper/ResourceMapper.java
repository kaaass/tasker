package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.ResourceEntity;
import net.kaaass.se.tasker.dto.ResourceDto;
import net.kaaass.se.tasker.vo.ResourceVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ResourceMapper {

    ResourceVo resourceDtoToVo(ResourceDto resourceDto);

    @Mapping(source = "uploader", target = "uploader", qualifiedByName = "userAuthEntityToDto")
    ResourceDto resourceEntityToDto(ResourceEntity resourceEntity);
}
