package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.controller.request.ManagerRequest;
import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import net.kaaass.se.tasker.dao.entity.ManagerEntity;
import net.kaaass.se.tasker.dto.EmployeeDto;
import net.kaaass.se.tasker.dto.ManagerDto;
import net.kaaass.se.tasker.dto.TaskDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 经理相关服务
 */
public interface ManagerService {

    Optional<EmployeeDto> addToGroup(String mid, String eid) throws ManagerNotFoundException, EmployeeNotFoundException;

    void deleteFromGroup(String mid, String eid) throws ManagerNotFoundException, EmployeeNotFoundException;

    List<EmployeeDto> getGroupMember(String mid);

    List<EmployeeEntity> getGroupMemberRaw(String mid);

    List<ManagerDto> getAll(Pageable pageable);

    ManagerDto update(String mid, ManagerRequest request) throws ManagerNotFoundException;

    ManagerDto add(ManagerRequest request) throws UserNotFoundException, BadRequestException;

    Optional<ManagerDto> getByUid(String uid);

    Optional<ManagerEntity> getEntity(String mid);

    List<TaskDto> listTaskForManager(String mid) throws ManagerNotFoundException;
}
