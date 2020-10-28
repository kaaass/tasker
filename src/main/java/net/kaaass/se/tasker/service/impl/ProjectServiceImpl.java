package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.TaskerApplication;
import net.kaaass.se.tasker.controller.request.GenerateProjectRequest;
import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import net.kaaass.se.tasker.dao.entity.TaskEntity;
import net.kaaass.se.tasker.dao.repository.ManagerRepository;
import net.kaaass.se.tasker.dao.repository.ProjectRepository;
import net.kaaass.se.tasker.dao.repository.TaskRepository;
import net.kaaass.se.tasker.dto.*;
import net.kaaass.se.tasker.event.ProjectCreateEvent;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
import net.kaaass.se.tasker.mapper.ProjectMapper;
import net.kaaass.se.tasker.mapper.ResourceMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
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

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceMapper resourceMapper;

    @Value("${file.templateDocumentPath}")
    private String templateDocumentPath;

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
    @Transactional
    public ProjectDto generateProject(GenerateProjectRequest request, String uid) throws NotFoundException {
        var project = new ProjectEntity();
        var tasks = new ArrayList<TaskEntity>();
        var rand = new Random();
        // 创建 tasks
        for (var type : TaskType.values()) {
            for (int i = 0; i < request.getTaskCounts().get(type); i++) {
                var newE = new TaskEntity();
                newE.setType(type);
                tasks.add(newE);
            }
        }
        Collections.shuffle(tasks);
        // 设置项目基本信息
        var manager = managerRepository.findByUserId(uid).orElseThrow(ManagerNotFoundException::new);
        project.setName(request.getName());
        project.setUndertaker(manager);
        project.setStatus(ProjectStatus.INACTIVE);
        // 查询可用人员
        var employeeMap = new HashMap<TaskType, List<EmployeeEntity>>();
        managerService.getGroupMemberRaw(manager.getId())
                .forEach(em -> {
                    var type = em.getType();
                    if (!employeeMap.containsKey(type)) {
                        employeeMap.put(type, new ArrayList<>());
                    }
                    employeeMap.get(type).add(em);
                });
        // 分配人员
        for (var task : tasks) {
            var type = task.getType();
            var employeePool = employeeMap.get(type);
            if (employeePool.isEmpty()) {
                throw new NotFoundException(String.format("经理组内没有足够的 %s 类员工！", type.toString()));
            }
            // 随机分配一个人
            var selected = rand.nextInt(employeePool.size());
            task.setUndertaker(employeePool.get(selected));
        }
        // 设置任务基本信息
        var typeCount = new HashMap<TaskType, Integer>();
        for (var task : tasks) {
            var type = task.getType();
            // 类型计数更新
            if (!typeCount.containsKey(type)) {
                typeCount.put(type, 0);
            }
            typeCount.put(type, typeCount.get(type) + 1);
            // 设置信息
            task.setName(String.format("Task_%s_%d", type.toString(), typeCount.get(type)));
            task.setStatus(TaskStatus.CREATED);
        }
        // 先保存任务，获得id
        for (int i = 0; i < tasks.size(); i++) {
            tasks.set(i, taskRepository.save(tasks.get(i)));
            tasks.get(i).setPrevious(new HashSet<>());
        }
        // 创建边依赖关系：前 -> 后
        int edgeCount = request.getTotal() * 3 / 4;
        while (edgeCount-- > 0) {
            int from = rand.nextInt(tasks.size() - 1); // 最后一个必须是终点
            int to = from + 1 + rand.nextInt(tasks.size() - from - 1);
            tasks.get(to).getPrevious().add(tasks.get(from));
        }
        // 保存项目
        var result = repository.save(project);
        // 设置项目、偏序关系，保存任务
        tasks.forEach(task -> task.setProject(project));
        var savedTask = taskRepository.saveAll(tasks);
        result.setTasks(new HashSet<>(savedTask));
        result = repository.save(result);
        // 触发事件
        var resultDto = mapper.entityToDto(result);
        TaskerApplication.EVENT_BUS.post(new ProjectCreateEvent(resultDto));
        return resultDto;
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
    public ResourceDto getOrCreateProjectDocument(ProjectDto projectDto) throws NotFoundException, BadRequestException {
        var entity = getEntity(projectDto.getId()).orElseThrow(ProjectNotFoundException::new);
        var doc = entity.getDoc();
        // 若存在直接返回
        if (doc != null) {
            return resourceMapper.entityToDto(doc);
        }
        // 若不存在从模板创建一个资源
        var result = resourceService.createByUrl(templateDocumentPath,
                ResourceType.DOCUMENT,
                entity.getUndertaker().getId());
        return result.orElseThrow();
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
