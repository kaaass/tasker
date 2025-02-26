package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.controller.request.ManagerRequest;
import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import net.kaaass.se.tasker.dao.entity.ManagerEntity;
import net.kaaass.se.tasker.dao.repository.ManagerRepository;
import net.kaaass.se.tasker.dto.EmployeeDto;
import net.kaaass.se.tasker.dto.ManagerDto;
import net.kaaass.se.tasker.dto.TaskDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import net.kaaass.se.tasker.mapper.EmployeeMapper;
import net.kaaass.se.tasker.mapper.ManagerMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.service.ManagerService;
import net.kaaass.se.tasker.service.ProjectService;
import net.kaaass.se.tasker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 经理相关服务的具体实现
 */
@Service
@Slf4j
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepository repository;

    @Autowired
    private ManagerMapper mapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Optional<EmployeeDto> addToGroup(String mid, String eid)
            throws ManagerNotFoundException, EmployeeNotFoundException, BadRequestException {
        var manager = getEntity(mid).orElseThrow(ManagerNotFoundException::new);
        var employee = employeeService.getEntity(eid).orElseThrow(EmployeeNotFoundException::new);
        // 检查员工是否已经有经理
        if (employee.getManager() != null) {
            throw new BadRequestException("员工已经属于某经理管理，请先联系该经理移出该员工！");
        }
        // 增加员工
        var group = manager.getEmployeeGroup();
        group.add(employee);
        manager.setEmployeeGroup(group);
        employee.setManager(manager); // FIXME: 级联更改不生效
        repository.save(manager); // 保存更改
        return employeeService.getEntity(eid).map(employeeMapper::entityToDto);
    }

    @Override
    public void deleteFromGroup(String mid, String eid)
            throws ManagerNotFoundException, EmployeeNotFoundException {
        var manager = getEntity(mid).orElseThrow(ManagerNotFoundException::new);
        var employee = employeeService.getEntity(eid).orElseThrow(EmployeeNotFoundException::new);
        // 删除员工
        manager.getEmployeeGroup().remove(employee);
        employee.setManager(null); // FIXME: 级联更改不生效
        repository.save(manager); // 保存更改
    }

    @Override
    public List<EmployeeDto> getGroupMember(String mid) {
        return getEntity(mid)
                .map(ManagerEntity::getEmployeeGroup)
                .stream()
                .flatMap(Collection::stream)
                .map(employeeMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeEntity> getGroupMemberRaw(String mid) {
        return getEntity(mid)
                .map(ManagerEntity::getEmployeeGroup)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<ManagerDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ManagerDto update(String mid, ManagerRequest request) throws ManagerNotFoundException {
        var entity = getEntity(mid)
                .orElseThrow(ManagerNotFoundException::new);
        try {
            return saveBaseOnEntity(request, entity);
        } catch (UserNotFoundException | BadRequestException e) {
            // 不可能不存在
            log.warn("数据库可能存在不一致", e);
            return null;
        }
    }

    @Override
    public ManagerDto add(ManagerRequest request) throws UserNotFoundException, BadRequestException {
        userService.grant(request.getUid(), Role.MANAGER);
        return saveBaseOnEntity(request, new ManagerEntity());
    }

    private ManagerDto saveBaseOnEntity(ManagerRequest request, ManagerEntity entity) throws UserNotFoundException, BadRequestException {
        entity.setName(request.getName());
        if (request.getUid() != null)
            entity.setUser(userService.getEntity(request.getUid())
                    .orElseThrow(UserNotFoundException::new));
        ManagerEntity result;
        try {
            result = repository.saveAndFlush(entity);
        } catch (Exception e) {
            log.info("用户已经存在经理记录", e);
            throw new BadRequestException("用户已经存在经理记录！");
        }
        return mapper.entityToDto(result);
    }

    @Override
    public Optional<ManagerDto> getByUid(String uid) {
        return repository.findByUserId(uid).map(mapper::entityToDto);
    }

    @Override
    public Optional<ManagerEntity> getEntity(String mid) {
        return repository.findById(mid);
    }

    @Override
    public List<TaskDto> listTaskForManager(String mid) throws ManagerNotFoundException {
        var projects = projectService.getAllForManagerEntity(mid, Pageable.unpaged());
        return projects.stream()
                .flatMap(project -> project.getTasks().stream())
                .map(taskMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteManager(String mid) throws ManagerNotFoundException {
        var entity = getEntity(mid).orElseThrow(ManagerNotFoundException::new);
        entity.setDeleted(true);
        repository.saveAndFlush(entity);
    }
}
