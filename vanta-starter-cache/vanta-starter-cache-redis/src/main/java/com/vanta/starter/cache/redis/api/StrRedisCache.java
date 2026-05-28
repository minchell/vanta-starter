package com.vanta.starter.cache.redis.api;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * StrRedisCache 类。
 * <p>该类型属于 缓存能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@Slf4j
public class StrRedisCache {
    /**
     * stringRedisTemplate 字段。
     * <p>用于保存 缓存能力 的底层客户端或模板依赖，业务方可以通过自定义 Bean 替换。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param stringRedisTemplate stringRedisTemplate 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public StrRedisCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 执行 expire 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key      key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param time     time 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param timeUnit timeUnit 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Boolean expire(String key, Long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                return stringRedisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 指定缓存失效时间(秒)
     */
    public Boolean expire(String key, Long time) {
        return expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 根据key获取过期时间
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key是否存在
     */
    public Boolean hasKey(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 删除缓存
     */
    public void del(String... keys) {
        if (ArrayUtil.isNotEmpty(keys)) {
            if (keys.length == 1) {
                stringRedisTemplate.delete(keys[0]);
            } else {
                stringRedisTemplate.delete(Arrays.asList(keys));
            }
        }
    }

    /**
     * 普通缓存获取
     */
    public String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     */
    public Boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 普通缓存放入并设置时间(秒)
     */
    public Boolean set(String key, String value, Long time) {
        try {
            if (time > 0) {
                stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
                return true;
            }
            return set(key, value);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 递增
     */
    public Long incr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     */
    public Long decr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return stringRedisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * HashGet
     */
    public Object hGet(String key, String item) {
        return stringRedisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取 hashKey对应的所有键值
     */
    public Map<Object, Object> hmGet(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     */
    public Boolean hmSet(String key, Map<String, String> map) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * HashSet 并设置时间
     */
    public Boolean hmSet(String key, Map<String, String> map, Long time) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     */
    public Boolean hset(String key, String item, Object value) {
        try {
            stringRedisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     */
    public Boolean hset(String key, String item, Object value, Long time) {
        try {
            stringRedisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 删除hash表中的值
     */
    public void hdel(String key, Object... item) {
        stringRedisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     */
    public Boolean hHasKey(String key, String item) {
        return stringRedisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     */
    public Long hIncr(String key, String item, Long by) {
        return stringRedisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     */
    public Double hIncr(String key, String item, Double by) {
        return stringRedisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     */
    public Double hDecr(String key, String item, Double by) {
        return stringRedisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * hash递减
     */
    public Long hDecr(String key, String item, Long by) {
        return stringRedisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 根据 key获取 Set中的所有值
     *
     * @param key 键
     * @return Set
     */
    public Set<String> sGet(String key) {
        try {
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return Collections.emptySet();
    }

    /**
     * 根据value从一个set中查询,是否存在
     */
    public Boolean sHasKey(String key, Object value) {
        try {
            return stringRedisTemplate.opsForSet().isMember(key, value);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 将数据放入set缓存
     */
    public Object sSet(String key, String... values) {
        try {
            return stringRedisTemplate.opsForSet().add(key, values);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 0L;
    }

    /**
     * 将set数据放入缓存
     */
    public Long sSetAndTime(String key, Long time, String... values) {
        try {
            Long count = stringRedisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 0L;
    }

    /**
     * 获取set缓存的长度
     */
    public Long sGetSetSize(String key) {
        try {
            return stringRedisTemplate.opsForSet().size(key);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 0L;
    }

    /**
     * 移除值为value的
     */
    public Long setRemove(String key, Object... values) {
        try {
            return stringRedisTemplate.opsForSet().remove(key, values);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 0L;
    }

    /**
     * 获取list缓存的内容
     */
    public List<String> lGet(String key, Long start, Long end) {
        try {
            return stringRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }

    /**
     * 获取list缓存的长度
     */
    public Long lGetListSize(String key) {
        try {
            return stringRedisTemplate.opsForList().size(key);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 0L;
    }

    /**
     * 通过索引 获取list中的值
     */
    public String lGetIndex(String key, Long index) {
        try {
            return stringRedisTemplate.opsForList().index(key, index);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 删除并返回列表的第一个元素
     */
    public String lLeftPop(String key) {
        try {
            return stringRedisTemplate.opsForList().leftPop(key);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 将list放入缓存
     */
    public Boolean lSet(String key, String value) {
        try {
            stringRedisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 将list放入缓存
     */
    public Boolean lSet(String key, String value, Long time) {
        try {
            stringRedisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 将list放入缓存
     */
    public Boolean lSet(String key, List<String> value) {
        try {
            stringRedisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 将list放入缓存
     */
    public Boolean lSet(String key, List<String> value, Long time) {
        try {
            stringRedisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 根据索引修改list中的某条数据
     */
    public Boolean lUpdateIndex(String key, Long index, String value) {
        try {
            stringRedisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 移除N个值为value
     */
    public Long lRemove(String key, Long count, Object value) {
        try {
            return stringRedisTemplate.opsForList().remove(key, count, value);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 0L;
    }

    /**
     * 执行 unlink 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param keys keys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public void unlink(Set<String> keys) {
        try {
            stringRedisTemplate.unlink(keys);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 设置 Bit 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param key    key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param offset offset 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param value  value 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Boolean setBit(String key, long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 读取 Bit 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @param key    key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param offset offset 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public boolean getBit(String key, long offset) {
        Boolean bit = stringRedisTemplate.opsForValue().getBit(key, offset);
        return Boolean.TRUE.equals(bit);
    }

    /**
     * 执行 bitField 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key         key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param subCommands subCommands 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public List<Long> bitField(String key, BitFieldSubCommands subCommands) {
        return stringRedisTemplate.opsForValue().bitField(key, subCommands);
    }

    /**
     * 执行 execute 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param action action 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> T execute(RedisCallback<T> action) {
        return stringRedisTemplate.execute(action);
    }

}
