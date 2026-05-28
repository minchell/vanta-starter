package com.vanta.starter.data.repository;

/**
 * Repository 治理边界标记接口。
 * <p>
 * 该接口故意不提供 save、list、page、remove 等通用 CRUD 方法。
 * Repository 必须由业务方根据领域语义定义方法，例如 findByEmail、recordLoginToken、
 * markDeviceOnline。这样可以防止业务层退化为表操作脚本。
 * </p>
 */
public interface BaseRepository {
}
