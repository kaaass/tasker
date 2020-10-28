package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.controller.request.DelegateRequest;
import net.kaaass.se.tasker.controller.request.TaskRequest;
import net.kaaass.se.tasker.dao.entity.TaskEntity;
import net.kaaass.se.tasker.dto.DelegateDto;
import net.kaaass.se.tasker.dto.ResourceDto;
import net.kaaass.se.tasker.dto.TaskDto;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
import net.kaaass.se.tasker.exception.concrete.TaskNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * 任务相关服务
 */
public interface TaskService {

    /**
     * 检查过期委托并收回权限
     *
     * 每次查询任务都 lazy 检查一下是否有过期的委托
     */
    void checkDelegateExpire();

    TaskDto update(String tid, TaskRequest request) throws TaskNotFoundException, ProjectNotFoundException, EmployeeNotFoundException, BadRequestException;

    TaskDto add(TaskRequest request) throws ProjectNotFoundException, EmployeeNotFoundException, BadRequestException;

    Optional<TaskDto> getById(String tid);

    Optional<TaskEntity> getEntity(String tid);

    TaskDto commitTask(String tid, String documentId) throws NotFoundException, BadRequestException;

    ResourceDto finishTask(String tid) throws NotFoundException, BadRequestException;

    TaskDto confirmTask(String tid) throws TaskNotFoundException, BadRequestException, ProjectNotFoundException;

    TaskDto rejectTask(String tid) throws TaskNotFoundException, BadRequestException;

    List<TaskDto> listTaskForEmployee(String eid) throws EmployeeNotFoundException;

    /**
     * 获得当前可以开始的任务
     *
     * 即前序任务全部完成、任务状态处于CREATED、INACTIVE的
     */
    List<TaskEntity> getReadyTaskForProject(String pid) throws ProjectNotFoundException;

    /**
     * 查看已委托给他人的任务
     */
    List<DelegateDto> listDelegateForEmployee(String eid) throws EmployeeNotFoundException;

    DelegateDto addDelegate(String tid, DelegateRequest request, String uid) throws TaskNotFoundException, EmployeeNotFoundException;

    TaskDto withdrawDelegate(String tid) throws NotFoundException;

    void checkViewPermit(String tid, UserDto userDto);
}
