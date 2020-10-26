package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import net.kaaass.se.tasker.dto.EmployeeDto;
import net.kaaass.se.tasker.vo.EmployeeVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 员工相关数据结构映射器
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, ManagerMapper.class})
public interface EmployeeMapper {

    EmployeeVo dtoToVo(EmployeeDto dto);

    EmployeeDto entityToDto(EmployeeEntity entity);
}
