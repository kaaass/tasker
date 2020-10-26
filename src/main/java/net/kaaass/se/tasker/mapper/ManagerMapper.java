package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.ManagerEntity;
import net.kaaass.se.tasker.dto.ManagerDto;
import net.kaaass.se.tasker.vo.ManagerVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 经理相关数据结构映射器
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ManagerMapper {

    ManagerVo dtoToVo(ManagerDto dto);

    ManagerDto entityToDto(ManagerEntity entity);
}
