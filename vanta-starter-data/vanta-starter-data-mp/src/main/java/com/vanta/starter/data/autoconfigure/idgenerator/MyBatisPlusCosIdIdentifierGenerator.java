package com.vanta.starter.data.autoconfigure.idgenerator;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import me.ahoo.cosid.snowflake.SnowflakeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

/**
 * CosId 到 MyBatis-Plus 的 ID 生成器适配器。
 * <p>
 * MyBatis-Plus 需要 {@link IdentifierGenerator} 接口，CosId 提供 {@link SnowflakeId}。
 * 该类把 CosId 的共享雪花生成器包装为 MyBatis-Plus 可识别的 ID 生成器。
 * </p>
 */
public class MyBatisPlusCosIdIdentifierGenerator implements IdentifierGenerator {

    /**
     * CosId 共享雪花 ID 生成器。
     */
    @Qualifier("__share__SnowflakeId")
    @Lazy
    @Autowired
    private SnowflakeId snowflakeId;

    /**
     * 生成下一个实体 ID。
     *
     * @param entity 当前待插入实体。
     * @return CosId 生成的雪花 ID。
     */
    @Override
    public Number nextId(Object entity) {
        return snowflakeId.generate();
    }
}
