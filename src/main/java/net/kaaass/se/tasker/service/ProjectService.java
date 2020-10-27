package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.controller.request.GenerateProjectRequest;
import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import net.kaaass.se.tasker.dto.*;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
import net.kaaass.se.tasker.vo.ProjectVo;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 项目相关服务
 */
public interface ProjectService {

    List<ProjectDto> getAll(Pageable pageable);

    List<ProjectDto> getAllForManager(ManagerDto managerDto, Pageable pageable) throws ManagerNotFoundException;

    List<ProjectDto> getAllForEmployee(EmployeeDto employeeDto, Pageable pageable);

    ProjectDto startProject(String pid) throws ProjectNotFoundException;

    ProjectDto stopProject(String pid) throws ProjectNotFoundException;

    List<TaskDto> getProjectTasks(String pid) throws ProjectNotFoundException;

    ProjectVo generateProject(GenerateProjectRequest request);

    Optional<ProjectDto> getByPid(String pid);

    Optional<ProjectEntity> getEntity(String pid);

    void checkViewProjectPermit(String pid, UserDto userDto) throws ProjectNotFoundException, ManagerNotFoundException, ForbiddenException, EmployeeNotFoundException;
}
