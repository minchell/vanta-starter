package com.vanta.starter.log.dao.impl;

import com.vanta.starter.log.dao.LogDao;
import com.vanta.starter.log.model.LogRecord;

import java.util.LinkedList;
import java.util.List;


/**
 * 默认内存操作日志实现。
 * <p>
 * 该实现不连接数据库、不写远程服务，只在内存中保留最近的有限日志记录，适合开发调试和无外部依赖的默认测试。
 * 生产项目应按审计要求提供自己的 {@link LogDao} 实现。
 * </p>
 */
public class DefaultLogDaoImpl implements LogDao {

    /**
     * 内存中的日志记录列表。
     * <p>
     * 所有读写都通过 synchronized 保护，避免并发请求同时写入时破坏列表结构。
     * </p>
     */
    private final List<LogRecord> logRecords = new LinkedList<>();
    /**
     * 内存最多保留的日志条数。
     * <p>
     * 超过容量时会按 {@link #reverse} 决定从列表头部或尾部移除旧记录。
     * </p>
     */
    private int capacity = 100;
    /**
     * 日志列表是否按最新记录在前排列。
     * <p>
     * {@code true} 表示新日志插入到列表头部；{@code false} 表示新日志追加到列表尾部。
     * </p>
     */
    private boolean reverse = true;

    /**
     * 返回当前内存中保存的日志快照。
     *
     * @return 不可变日志列表快照
     */
    @Override
    public List<LogRecord> list() {
        synchronized (this.logRecords) {
            return List.copyOf(this.logRecords);
        }
    }

    /**
     * 写入一条日志记录到内存列表。
     * <p>
     * 写入前会按容量清理旧数据，保证默认实现不会无限占用内存。
     * </p>
     *
     * @param logRecord 已完成组装的操作日志记录
     */
    @Override
    public void add(LogRecord logRecord) {
        synchronized (this.logRecords) {
            while (this.logRecords.size() >= this.capacity) {
                this.logRecords.remove(this.reverse ? this.capacity - 1 : 0);
            }
            if (this.reverse) {
                this.logRecords.add(0, logRecord);
            } else {
                this.logRecords.add(logRecord);
            }
        }
    }

    /**
     * 设置内存中最多保留的日志数量。
     *
     * @param capacity 最大日志条数
     */
    public void setCapacity(int capacity) {
        synchronized (this.logRecords) {
            this.capacity = capacity;
        }
    }

    /**
     * 设置日志列表排序方向。
     *
     * @param reverse {@code true} 表示最新日志在前；{@code false} 表示最新日志在后
     */
    public void setReverse(boolean reverse) {
        synchronized (this.logRecords) {
            this.reverse = reverse;
        }
    }
}
