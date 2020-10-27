package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.controller.request.EmployeeRequest;
import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import net.kaaass.se.tasker.dao.entity.ManagerEntity;
import net.kaaass.se.tasker.dto.EmployeeDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 员工相关服务
 */
public interface EmployeeService {

    List<EmployeeDto> getAllEmployee(Pageable pageable);

    EmployeeDto update(String eid, EmployeeRequest request) throws NotFoundException, BadRequestException;

    EmployeeDto add(EmployeeRequest request) throws BadRequestException, NotFoundException;

    Optional<EmployeeDto> getByUid(String uid);

    Optional<EmployeeEntity> getEntity(String eid);
}
