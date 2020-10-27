package net.kaaass.se.tasker.service;

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
}
