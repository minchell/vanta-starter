package com.vanta.starter.data.page;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * MyBatis-Plus 分页结果转换器。
 * <p>
 * 该工具只能在 Repository 实现内部使用，用于把 MyBatis-Plus {@link IPage}
 * 转换为 starter 暴露给业务层的 {@link PageResult}。
 * </p>
 */
public final class PageConverter {

    /**
     * 私有构造，禁止实例化工具类。
     */
    private PageConverter() {
    }

    /**
     * 转换 MyBatis-Plus 分页结果。
     *
     * @param page MyBatis-Plus 分页结果。
     * @param <T> 记录类型。
     * @return 统一分页结果。
     */
    public static <T> PageResult<T> from(IPage<T> page) {
        if (page == null) {
            return PageResult.empty(new PageQuery());
        }
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
}
