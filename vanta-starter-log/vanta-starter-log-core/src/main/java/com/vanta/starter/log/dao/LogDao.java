package com.vanta.starter.log.dao;

import com.vanta.starter.log.model.LogRecord;

import java.util.Collections;
import java.util.List;


/**
 * 操作日志写入端口。
 * <p>
 * starter 默认只提供内存实现，业务项目可以实现该接口把日志写入数据库、消息队列、审计平台或可观测系统。
 * 接口保持最小化，是为了让日志 starter 离开具体业务项目后仍然容易被替换和复用。
 * </p>
 */
public interface LogDao {

    /**
     * 查询当前日志实现中可回放的日志列表。
     * <p>
     * 对于落库实现，该方法可以返回分页前的有限列表；对于只写不读的实现，可以保持默认空列表。
     * </p>
     *
     * @return 日志记录列表，默认返回空集合
     */
    default List<LogRecord> list() {
        return Collections.emptyList();
    }

    /**
     * 写入一条操作日志记录。
     *
     * @param logRecord 已完成组装的操作日志记录
     */
    void add(LogRecord logRecord);
}
