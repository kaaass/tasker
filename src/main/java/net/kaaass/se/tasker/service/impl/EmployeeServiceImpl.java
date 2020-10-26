package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.controller.request.EmployeeRequest;
import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import net.kaaass.se.tasker.dao.repository.EmployeeRepository;
import net.kaaass.se.tasker.dto.EmployeeDto;
import net.kaaass.se.tasker.dto.EmployeeType;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.mapper.EmployeeMapper;
import net.kaaass.se.tasker.service.AuthService;
import net.kaaass.se.tasker.service.EmployeeService;
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
    private AuthService authService;

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
        return saveBaseOnEntity(request, entity);
    }

    private EmployeeDto saveBaseOnEntity(EmployeeRequest request, EmployeeEntity entity)
            throws BadRequestException, NotFoundException {
        var type = EmployeeType.valueOfThrow(request.getType());
        entity.setName(request.getName());
        entity.setType(type);
        if (request.getUid() != null)
            entity.setUser(authService.getEntity(request.getUid())
                    .orElseThrow(() -> new NotFoundException("该用户不存在！")));
        var result = repository.save(entity);
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
