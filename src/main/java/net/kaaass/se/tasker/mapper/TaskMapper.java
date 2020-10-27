package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.DelegateEntity;
import net.kaaass.se.tasker.dao.entity.TaskEntity;
import net.kaaass.se.tasker.dto.DelegateDto;
import net.kaaass.se.tasker.dto.TaskDto;
import net.kaaass.se.tasker.vo.DelegateVo;
import net.kaaass.se.tasker.vo.TaskVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 项目相关数据结构映射器
 */
@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class, ManagerMapper.class, EmployeeMapper.class, UserMapper.class})
public interface TaskMapper {

    TaskVo dtoToVo(TaskDto dto);

    @Named("isDelegatePresents")
    default boolean isDelegatePresents(DelegateEntity delegateEntity) {
        return delegateEntity != null;
    }

    @Mapping(source = "delegate", target = "delegate", qualifiedByName = "isDelegatePresents")
    TaskDto entityToDto(TaskEntity entity);

    DelegateVo dtoToVo(DelegateDto dto);

    DelegateDto entityToDto(DelegateEntity entity);
}
