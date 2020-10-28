package net.kaaass.se.tasker.dto;

/**
 * 任务状态
 *
 *                     INACTIVE <---------------+------------------  REJECTED
 *                        |↑    /project/stop   | /task/finish          ↑ /task/reject
 *        /project/start  ↓|                    ↓                       |
 * CREATED -----------> ACTIVE -----------> WAIT_COMMIT -----------> WAIT_REVIEW -----------> DONE
 *                            /task/finish             /task/commit            /task/confirm
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
     * 等待检查
     */
    WAIT_REVIEW,

    /**
     * 完成
     */
    DONE,

    /**
     * 被打回
     */
    REJECTED
}
