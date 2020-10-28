package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.TaskerApplication;
import net.kaaass.se.tasker.controller.request.DelegateRequest;
import net.kaaass.se.tasker.controller.request.TaskRequest;
import net.kaaass.se.tasker.dao.entity.DelegateEntity;
import net.kaaass.se.tasker.dao.entity.TaskEntity;
import net.kaaass.se.tasker.dao.repository.DelegateRepository;
import net.kaaass.se.tasker.dao.repository.EmployeeRepository;
import net.kaaass.se.tasker.dao.repository.TaskRepository;
import net.kaaass.se.tasker.dto.*;
import net.kaaass.se.tasker.event.TaskFinishEvent;
import net.kaaass.se.tasker.event.TaskStartEvent;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
import net.kaaass.se.tasker.exception.concrete.TaskNotFoundException;
import net.kaaass.se.tasker.mapper.ProjectMapper;
import net.kaaass.se.tasker.mapper.ResourceMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.service.ProjectService;
import net.kaaass.se.tasker.service.ResourceService;
import net.kaaass.se.tasker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务相关服务的具体实现
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DelegateRepository delegateRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public void checkDelegateExpire() {
        // 找所有当前时间之前结束的委托
        var delegates = delegateRepository.findAllByExpireBefore(Timestamp.valueOf(LocalDateTime.now()));
        // 对每个委托，撤销
        delegates.forEach(delegateEntity -> {
            try {
                this.withdrawDelegate(delegateEntity.getTask().getId());
            } catch (NotFoundException ignore) {
                // 不可能不存在
            }
        });
    }

    @Override
    public TaskDto update(String tid, TaskRequest request) throws TaskNotFoundException, ProjectNotFoundException, EmployeeNotFoundException, BadRequestException {
        checkDelegateExpire();
        var entity = getEntity(tid).orElseThrow(TaskNotFoundException::new);
        return saveBaseOnEntity(request, entity);
    }

    @Override
    public TaskDto add(TaskRequest request) throws ProjectNotFoundException, EmployeeNotFoundException, BadRequestException {
        return saveBaseOnEntity(request, new TaskEntity());
    }

    private TaskDto saveBaseOnEntity(TaskRequest request, TaskEntity entity)
            throws EmployeeNotFoundException, ProjectNotFoundException, BadRequestException {
        var previous = request.getPreviousId().stream()
                .map(this::getEntity)
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet());
        var undertaker = employeeService.getEntity(request.getUndertakerEid())
                .orElseThrow(EmployeeNotFoundException::new);
        var project = projectService.getEntity(request.getProjectId())
                .orElseThrow(ProjectNotFoundException::new);
        var type = TaskType.valueOfThrow(request.getType());
        // 设置
        entity.setName(request.getName());
        entity.setType(type);
        entity.setUndertaker(undertaker);
        entity.setStatus(TaskStatus.CREATED);
        entity.setProject(project);
        entity.setPrevious(previous);
        return mapper.entityToDto(repository.save(entity));
    }

    @Override
    public Optional<TaskDto> getById(String tid) {
        checkDelegateExpire();
        return getEntity(tid).map(mapper::entityToDto);
    }

    @Override
    public Optional<TaskEntity> getEntity(String tid) {
        return repository.findById(tid);
    }

    public TaskEntity getEntityRaw(String tid) throws TaskNotFoundException {
        return getEntity(tid).orElseThrow(TaskNotFoundException::new);
    }

    @Override
    public TaskDto commitTask(String tid, String documentId) throws NotFoundException, BadRequestException {
        checkDelegateExpire();
        var entity = getEntityRaw(tid);
        // 送审必须是 WAIT_COMMIT 状态
        if (entity.getStatus() != TaskStatus.WAIT_COMMIT) {
            throw new BadRequestException("送审必须是 WAIT_COMMIT 状态！");
        }
        // 加载文档
        var document = resourceService.getEntity(documentId)
                .orElseThrow(() -> new NotFoundException("文档不存在！"));
        // 送审后设置为 WAIT_REVIEW 状态
        entity.setStatus(TaskStatus.WAIT_REVIEW);
        entity.setPending(document);
        var result = repository.save(entity);
        return mapper.entityToDto(result);
    }

    @Override
    @Transactional
    public ResourceDto finishTask(String tid) throws NotFoundException, BadRequestException {
        checkDelegateExpire();
        var entity = getEntityRaw(tid);
        // 完成必须是 ACTIVE 或 REJECTED 状态
        if (entity.getStatus() != TaskStatus.ACTIVE &&
            entity.getStatus() != TaskStatus.REJECTED) {
            throw new BadRequestException("送审必须是 ACTIVE 或 REJECTED 状态！");
        }
        // 同一时间一个项目处于提交状态的任务只能有一个
        var hasOtherFinishTask = repository.existsByProjectAndStatus(entity.getProject(), TaskStatus.WAIT_COMMIT);
        if (hasOtherFinishTask) {
            throw new BadRequestException("同一时间一个项目处于提交状态的任务只能有一个！");
        }
        // 完成后设置为 WAIT_COMMIT 状态
        entity.setStatus(TaskStatus.WAIT_COMMIT);
        // 获得项目文档路径
        var document = projectService.getOrCreateProjectDocument(projectMapper.entityToDto(entity.getProject()));
        // FIXME 此处错误不会回滚
        entity.setPending(document);
        // 保存
        repository.save(entity);
        return resourceMapper.entityToDto(document);
    }

    @Override
    @Transactional
    public TaskDto confirmTask(String tid) throws TaskNotFoundException, BadRequestException, ProjectNotFoundException {
        checkDelegateExpire();
        var entity = getEntityRaw(tid);
        // 确认必须是 WAIT_REVIEW 状态
        if (entity.getStatus() != TaskStatus.WAIT_REVIEW) {
            throw new BadRequestException("确认必须是 WAIT_REVIEW 状态！");
        }
        // 确认后设置为 DONE 状态
        entity.setStatus(TaskStatus.DONE);
        // 更新项目文档
        var document = entity.getPending();
        var project = entity.getProject();
        project.setDoc(document);
        // 保存
        entity = repository.save(entity);
        var result = mapper.entityToDto(entity);
        // 触发任务完成事件
        TaskerApplication.EVENT_BUS.post(new TaskFinishEvent(result));
        // 检查项目是否结束
        project = projectService.checkProjectDone(project.getId());
        // 如果项目在活动中，则开始新的任务
        if (project.getStatus() == ProjectStatus.ACTIVE) {
            // 获取并更新最新任务
            var updateTasks = new ArrayList<TaskEntity>();
            getReadyTaskForProject(project.getId()).forEach(task -> {
                task.setStatus(TaskStatus.ACTIVE);
                updateTasks.add(task);
            });
            for (var task : updateTasks) {
                repository.save(task);
            }
            // 触发任务开始事件
            if (!updateTasks.isEmpty()) {
                TaskerApplication.EVENT_BUS.post(new TaskStartEvent(
                        updateTasks.stream()
                                .map(mapper::entityToDto)
                                .collect(Collectors.toList())
                ));
            }
        }
        // 更新返回结果
        result = getById(entity.getId()).orElseThrow();
        return result;
    }

    @Override
    public TaskDto rejectTask(String tid) throws TaskNotFoundException, BadRequestException {
        checkDelegateExpire();
        var entity = getEntityRaw(tid);
        // 拒绝必须是 WAIT_REVIEW 状态
        if (entity.getStatus() != TaskStatus.WAIT_REVIEW) {
            throw new BadRequestException("拒绝必须是 WAIT_REVIEW 状态！");
        }
        // 拒绝后设置为 REJECTED 状态
        entity.setStatus(TaskStatus.REJECTED);
        var result = repository.save(entity);
        return mapper.entityToDto(result);
    }

    @Override
    public List<TaskDto> listTaskForEmployee(String eid) throws EmployeeNotFoundException {
        checkDelegateExpire();
        var employee = employeeService.getEntity(eid).orElseThrow(EmployeeNotFoundException::new);
        return employee.getOwnedTasks().stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> getReadyTaskForProject(String pid) throws ProjectNotFoundException {
        var project = projectService.getEntity(pid).orElseThrow(ProjectNotFoundException::new);
        var tasks = project.getTasks();
        // 构建任务状态表
        var statusMap = new HashMap<String, TaskStatus>();
        tasks.forEach(taskDto -> statusMap.put(taskDto.getId(), taskDto.getStatus()));
        // 筛选
        return tasks.parallelStream()
                // 根据当前状态
                .filter(task -> task.getStatus() == TaskStatus.CREATED ||
                        task.getStatus() == TaskStatus.INACTIVE)
                // 之前任务状态都为 DONE
                .filter(task -> task.getPrevious().parallelStream()
                        .allMatch(taskDto -> statusMap.get(taskDto.getId()) == TaskStatus.DONE))
                .collect(Collectors.toList());
    }

    @Override
    public List<DelegateDto> listDelegateForEmployee(String eid) throws EmployeeNotFoundException {
        var employee = employeeService.getEntity(eid).orElseThrow(EmployeeNotFoundException::new);
        return employee.getGiveOutDelegates().stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DelegateDto addDelegate(String tid, DelegateRequest request, String uid) throws TaskNotFoundException, EmployeeNotFoundException {
        var delegate = new DelegateEntity();
        // 解析
        var entity = getEntityRaw(tid);
        var delegateTo = employeeService.getEntity(request.getDelegateTo())
                .orElseThrow(EmployeeNotFoundException::new);
        var me = employeeRepository.findByUserId(uid).orElseThrow(EmployeeNotFoundException::new);
        // 创建
        delegate.setTask(entity);
        delegate.setFrom(me);
        delegate.setDelegateTo(delegateTo);
        delegate.setExpire(Timestamp.valueOf(request.getExpire()));
        // 保存
        var result = delegateRepository.save(delegate);
        // 修改原任务的分配者
        entity.setUndertaker(delegateTo);
        entity.setDelegate(result);
        repository.save(entity);
        return mapper.entityToDto(result);
    }

    @Override
    @Transactional
    public TaskDto withdrawDelegate(String tid) throws NotFoundException {
        checkDelegateExpire();
        var entity = getEntityRaw(tid);
        var delegate = entity.getDelegate();
        if (delegate == null) {
            throw new NotFoundException("该任务没有委托！");
        }
        // 删除委托
        delegateRepository.delete(delegate);
        // 恢复原任务的分配者，设置delegate
        entity.setUndertaker(delegate.getFrom());
        entity.setDelegate(null);
        var result = repository.save(entity);
        return mapper.entityToDto(result);
    }

    @Override
    public void checkViewPermit(String tid, UserDto userDto) throws EmployeeNotFoundException, ForbiddenException {
        var roles = userDto.getRoles();
        if (roles.contains(Role.ADMIN)) {
            // 管理员允许一切
            return;
        } else if (roles.contains(Role.MANAGER)) {
            // 经理必须是他有的项目
            // TODO 检查查看任务权限，不过id不连续，应该很难猜
        } else if (roles.contains(Role.EMPLOYEE)) {
            // 员工必须是被分配任务的项目
            var employee = employeeRepository.findByUserId(userDto.getId())
                    .orElseThrow(EmployeeNotFoundException::new);
            if (!repository.existsByIdAndUndertaker(tid, employee)) {
                throw new ForbiddenException("无权查看、操作此任务！");
            }
        }
    }
}
