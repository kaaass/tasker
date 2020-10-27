package net.kaaass.se.tasker.mapper;

import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import net.kaaass.se.tasker.dto.ProjectDto;
import net.kaaass.se.tasker.vo.ProjectVo;
import org.mapstruct.Mapper;

/**
 * 项目相关数据结构映射器
 */
@Mapper(componentModel = "spring", uses = {ManagerMapper.class, ResourceMapper.class})
public interface ProjectMapper {

    ProjectVo dtoToVo(ProjectDto dto);

    ProjectDto entityToDto(ProjectEntity entity);
}
