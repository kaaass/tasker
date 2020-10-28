package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.controller.request.GenerateProjectRequest;
import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import net.kaaass.se.tasker.dao.entity.ResourceEntity;
import net.kaaass.se.tasker.dto.*;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
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

    ProjectDto startProject(String pid) throws ProjectNotFoundException, BadRequestException;

    ProjectDto stopProject(String pid) throws ProjectNotFoundException, BadRequestException;

    List<TaskDto> getProjectTasks(String pid) throws ProjectNotFoundException;

    ProjectDto generateProject(GenerateProjectRequest request, String uid) throws NotFoundException;

    Optional<ProjectDto> getByPid(String pid);

    Optional<ProjectEntity> getEntity(String pid);

    ResourceEntity getOrCreateProjectDocument(ProjectDto projectDto) throws NotFoundException, BadRequestException;

    ProjectEntity checkProjectDone(String pid) throws ProjectNotFoundException;

    void checkViewPermit(String pid, UserDto userDto) throws ProjectNotFoundException, ManagerNotFoundException, ForbiddenException, EmployeeNotFoundException;
}
