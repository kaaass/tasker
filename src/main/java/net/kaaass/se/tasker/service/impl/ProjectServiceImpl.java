package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.controller.request.GenerateProjectRequest;
import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import net.kaaass.se.tasker.dao.entity.TaskEntity;
import net.kaaass.se.tasker.dao.repository.ManagerRepository;
import net.kaaass.se.tasker.dao.repository.ProjectRepository;
import net.kaaass.se.tasker.dao.repository.TaskRepository;
import net.kaaass.se.tasker.dto.*;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
import net.kaaass.se.tasker.mapper.ProjectMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.service.ManagerService;
import net.kaaass.se.tasker.service.ProjectService;
import net.kaaass.se.tasker.service.TaskService;
import net.kaaass.se.tasker.vo.ProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 项目相关服务的具体实现
 */
@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectMapper mapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<ProjectDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getAllForManager(ManagerDto managerDto, Pageable pageable) throws ManagerNotFoundException {
        var entity = managerService.getEntity(managerDto.getId())
                .orElseThrow(ManagerNotFoundException::new);
        return entity.getOwnedProjects().stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getAllForEmployee(EmployeeDto employeeDto, Pageable pageable) {
        return repository.findAllOfEmployee(employeeDto.getId(), pageable).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto startProject(String pid) throws ProjectNotFoundException {
        var entity = getEntity(pid).orElseThrow(ProjectNotFoundException::new);
        entity.setStatus(ProjectStatus.ACTIVE);
        // 允许的 CREATED、INACTIVE 的变成 ACTIVE
        var updateTasks = new ArrayList<TaskEntity>();
        taskService.getReadyTaskForProject(pid).stream()
                .filter(task -> task.getStatus() == TaskStatus.CREATED ||
                        task.getStatus() == TaskStatus.INACTIVE)
                .forEach(task -> {
                    task.setStatus(TaskStatus.ACTIVE);
                    updateTasks.add(task);
                });
        repository.save(entity);
        for (var task : updateTasks) {
            taskRepository.save(task);
        }
        return mapper.entityToDto(entity);
    }

    @Override
    public ProjectDto stopProject(String pid) throws ProjectNotFoundException {
        var entity = getEntity(pid).orElseThrow(ProjectNotFoundException::new);
        entity.setStatus(ProjectStatus.ACTIVE);
        // 所有 ACTIVE、REJECTED 的变成 INACTIVE
        entity.getTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.ACTIVE ||
                        task.getStatus() == TaskStatus.REJECTED)
                .forEach(task -> task.setStatus(TaskStatus.INACTIVE));
        repository.save(entity);
        return mapper.entityToDto(entity);
    }

    @Override
    public List<TaskDto> getProjectTasks(String pid) throws ProjectNotFoundException {
        taskService.checkDelegateExpire();
        var entity = getEntity(pid).orElseThrow(ProjectNotFoundException::new);
        return entity.getTasks().stream()
                .map(taskMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectVo generateProject(GenerateProjectRequest request) {
        // TODO
        return null;
    }

    @Override
    public Optional<ProjectDto> getByPid(String pid) {
        return getEntity(pid).map(mapper::entityToDto);
    }

    @Override
    public Optional<ProjectEntity> getEntity(String pid) {
        return repository.findById(pid);
    }

    @Override
    public ResourceDto getOrCreateProjectDocument(ProjectDto projectDto) {
        // TODO
        return null;
    }

    @Override
    public ProjectEntity checkProjectDone(String pid) throws ProjectNotFoundException {
        var entity = getEntity(pid).orElseThrow(ProjectNotFoundException::new);
        var hasRestTask = taskRepository.existsByProjectAndStatusIsNot(entity, TaskStatus.DONE);
        // 没有更多的项目了，项目结束
        if (!hasRestTask) {
            entity.setStatus(ProjectStatus.DONE);
            entity = repository.save(entity);
        }
        return entity;
    }

    @Override
    public void checkViewPermit(String pid, UserDto userDto)
            throws ProjectNotFoundException, ManagerNotFoundException, ForbiddenException, EmployeeNotFoundException {
        var entity = getEntity(pid).orElseThrow(ProjectNotFoundException::new);
        var roles = userDto.getRoles();
        if (roles.contains(Role.ADMIN)) {
            // 管理员允许一切
            return;
        } else if (roles.contains(Role.MANAGER)) {
            // 经理必须是他有的项目
            var manager = managerRepository.findByUserId(userDto.getId())
                    .orElseThrow(ManagerNotFoundException::new);
            if (!repository.existsByIdAndUndertaker(pid, manager)) {
                throw new ForbiddenException("无权查看此项目！");
            }
        } else if (roles.contains(Role.EMPLOYEE)) {
            // 员工必须是被分配任务的项目
            // TODO 性能有点烂
            var employee = employeeService.getByUid(userDto.getId())
                    .orElseThrow(EmployeeNotFoundException::new);
            var projects = getAllForEmployee(employee, Pageable.unpaged());
            boolean isInProjects = projects.stream()
                    .anyMatch(projectDto -> projectDto.getId().equals(pid));
            if (!isInProjects) {
                throw new ForbiddenException("无权查看此项目！");
            }
        }
    }
}
