package net.kaaass.se.tasker.dto;

/**
 * 任务状态
 */
public enum TaskStatus {

    /**
     * 创建
     */
    CREATED,

    /**
     * 执行中
     */
    ACTIVE,

    /**
     * 随项目暂停
     */
    INACTIVE,

    /**
     * 等待提交
     */
    WAIT_COMMIT,

    /**
     * 完成
     */
    DONE,

    /**
     * 被打回
     */
    REJECTED
}
