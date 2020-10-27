package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.controller.request.EmployeeRequest;
import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import net.kaaass.se.tasker.dao.repository.EmployeeRepository;
import net.kaaass.se.tasker.dto.EmployeeDto;
import net.kaaass.se.tasker.dto.TaskType;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import net.kaaass.se.tasker.mapper.EmployeeMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 员工相关服务的具体实现
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private EmployeeMapper mapper;

    @Autowired
    private UserService userService;

    @Override
    public List<EmployeeDto> getAllEmployee(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto update(String eid, EmployeeRequest request)
            throws NotFoundException, BadRequestException {
        var entity = getEntity(eid)
                .orElseThrow(() -> new NotFoundException("员工信息不存在！"));
        return saveBaseOnEntity(request, entity);
    }

    @Override
    public EmployeeDto add(EmployeeRequest request) throws BadRequestException, NotFoundException {
        var entity = new EmployeeEntity();
        userService.grant(request.getUid(), Role.EMPLOYEE);
        return saveBaseOnEntity(request, entity);
    }

    private EmployeeDto saveBaseOnEntity(EmployeeRequest request, EmployeeEntity entity)
            throws BadRequestException, UserNotFoundException {
        var type = TaskType.valueOfThrow(request.getType());
        entity.setName(request.getName());
        entity.setType(type);
        if (request.getUid() != null)
            entity.setUser(userService.getEntity(request.getUid())
                    .orElseThrow(UserNotFoundException::new));
        EmployeeEntity result;
        try {
            result = repository.saveAndFlush(entity);
        } catch (Exception e) {
            throw new BadRequestException("用户已经存在员工记录！");
        }
        return mapper.entityToDto(result);
    }

    @Override
    public Optional<EmployeeDto> getByUid(String uid) {
        return repository.findByUserId(uid)
                .map(mapper::entityToDto);
    }

    @Override
    public Optional<EmployeeEntity> getEntity(String eid) {
        return repository.findById(eid);
    }

}
