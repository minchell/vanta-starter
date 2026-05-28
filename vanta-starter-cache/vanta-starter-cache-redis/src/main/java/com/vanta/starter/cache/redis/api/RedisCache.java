package com.vanta.starter.cache.redis.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * RedisCache 类。
 * <p>该类型属于 缓存能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue"})
public class RedisCache {

    /**
     * redisTemplate 字段。
     * <p>用于保存 缓存能力 的底层客户端或模板依赖，业务方可以通过自定义 Bean 替换。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final RedisTemplate<String, Object> redisTemplate;    public static final int BATCH_SIZE = 1000, HALF_BATCH_SIZE = BATCH_SIZE / 2;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param redisTemplate redisTemplate 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public RedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 执行脚本
     *
     * @param script The script to execute
     * @param keys   Any keys that need to be passed to the script
     * @param args   Any args that need to be passed to the script
     * @return The return value of the script or null if RedisScript.getResultType() is null, likely indicating a throw-away status reply (i.e. "OK")
     */
    public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
        return redisTemplate.execute(script, keys, args);
    }

    /**
     * 执行 executePipelined 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param action action 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> List<T> executePipelined(RedisCallback<?> action) {
        return (List<T>) redisTemplate.executePipelined(action);
    }

    /**
     * 执行 executePipelined 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param consumer consumer 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> List<T> executePipelined(Consumer<RedisConnection> consumer) {
        return executePipelined((RedisCallback<?>) conn -> {
            consumer.accept(conn);
            return null;
        });
    }

    /**
     * 执行 executePipelined 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param sessionCallback sessionCallback 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> List<T> executePipelined(SessionCallback<?> sessionCallback) {
        return (List<T>) redisTemplate.executePipelined(sessionCallback);
    }

    /**
     * 执行 executePipelined2 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param consumer consumer 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> List<T> executePipelined2(Consumer<RedisOperations<?, ?>> consumer) {
        return executePipelined(new SessionCallback<>() {
            /**
             * 执行 execute 逻辑。
             * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @param operations operations 参数，调用方应传入与 缓存能力 场景匹配的有效值
             * @return 方法执行后的结果对象、配置值或运行时依赖
             * @throws DataAccessException 当底层客户端、配置解析或远程调用失败时抛出
             */
            @SuppressWarnings("NullableProblems")
            @Override
            public <K, V> T execute(RedisOperations<K, V> operations) throws DataAccessException {
                consumer.accept(operations);
                return null;
            }
        });
    }

    /**
     * 执行 pipeline 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param consumer consumer 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public List<Object> pipeline(Consumer<RedisOperations<String, Object>> consumer) {

        return redisTemplate.executePipelined(new SessionCallback<>() {

            /**
             * 执行 execute 逻辑。
             * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @param operations operations 参数，调用方应传入与 缓存能力 场景匹配的有效值
             * @return 方法执行后的结果对象、配置值或运行时依赖
             * @throws DataAccessException 当底层客户端、配置解析或远程调用失败时抛出
             */
            @SuppressWarnings("unchecked")
            @Override
            public <K, V> Object execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                consumer.accept((RedisOperations<String, Object>) operations);
                return null;
            }
        });
    }

    /**
     * 执行 boundSetOps 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public BoundSetOperations<String, Object> boundSetOps(final String key) {
        return redisTemplate.boundSetOps(key);
    }

    /**
     * 执行 boundZSetOps 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public BoundZSetOperations<String, Object> boundZSetOps(final String key) {
        return redisTemplate.boundZSetOps(key);
    }

    /**
     * 根据key删除缓存对象
     */
    public Boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除给到的keys
     *
     * @return 已删除keys的数量. 被使用在pipeline / transaction中返回null
     */
    public Long deleteObject(final Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 判断 key是否存在
     */
    public Boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 批量判断key是否存在
     */
    public <T, R> Map<T, Boolean> batchHasKeys(Collection<T> keys, Function<T, R> keyMapper) {
        List<T> keyList = new ArrayList<>(new HashSet<>(keys));
        List<Boolean> results = executePipelined(new SessionCallback<>() {

            /**
             * 执行 execute 逻辑。
             * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @param operations operations 参数，调用方应传入与 缓存能力 场景匹配的有效值
             * @return 方法执行后的结果对象、配置值或运行时依赖
             * @throws DataAccessException 当底层客户端、配置解析或远程调用失败时抛出
             */
            @SuppressWarnings("NullableProblems")
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                keyList.forEach(key -> operations.hasKey((K) keyMapper.apply(key)));
                return null;
            }
        });
        Map<T, Boolean> resultMap = new HashMap<>();
        for (int i = 0; i < keyList.size(); i++) {
            resultMap.put(keyList.get(i), results.get(i));
        }
        return resultMap;
    }

    /**
     * 批量判断key是否存在
     */
    public <T> Map<T, Boolean> batchHasKeys(Collection<T> keys) {
        return batchHasKeys(keys, Function.identity());
    }

    /**
     * 批量判断key是否存在
     */
    public <T, R> Map<T, Boolean> batchHasKeys(Collection<T> keys, int batchSize, Function<T, R> function) {
        Map<T, Boolean> resultMap = new HashMap<>();
        Lists.partition(new ArrayList<>(keys), batchSize).forEach(lst -> resultMap.putAll(batchHasKeys(lst, function)));
        return resultMap;
    }

    /**
     * 批量判断key是否存在
     */
    public <T> Map<T, Boolean> batchHasKeys(Collection<T> keys, int batchSize) {
        return batchHasKeys(keys, batchSize, Function.identity());
    }

    /**
     * 设置有效时间
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 设置有效时间(单位：秒)
     */
    public Boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取有效时间
     */
    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 执行 opsForValue 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public ValueOperations<String, Object> opsForValue() {
        return redisTemplate.opsForValue();
    }

    /**
     * 缓存对象
     */
    public <T> void setCacheObject(final String key, final T value) {
        opsForValue().set(key, value);
    }

    /**
     * 缓存对象
     */
    public <T> void setCacheObject(final String key, final T value, final long timeout, final TimeUnit timeUnit) {
        opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 缓存对象(单位：秒)
     */
    public <T> void setCacheObject(final String key, final T value, final long timeout) {
        setCacheObject(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获得缓存对象
     */
    public <T> T getCacheObject(final String key) {
        return (T) opsForValue().get(key);
    }

    /**
     * 将key下存储为字符串值的整数值增加1
     */
    public <T> Long increment(final String key) {
        return opsForValue().increment(key);
    }

    /**
     * 将key下存储为字符串值的整数值增加delta
     */
    public <T> Long increment(final String key, final long delta) {
        return opsForValue().increment(key, delta);
    }

    /**
     * 减少数值
     */
    public <T> Long decrement(final String key, final long delta) {
        return opsForValue().decrement(key, delta);
    }

    /**
     * 删除缓存对象且返回
     */
    public <T> T getCacheObjectWithDel(final String key) {
        return (T) opsForValue().getAndDelete(key);
    }

    /**
     * setIfAbsent
     */
    public <T> Boolean setIfAbsent(final String key, final T value, long timeout, TimeUnit unit) {
        return opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * setIfAbsent
     */
    public <T> Boolean setIfAbsent(final String key, final T value, long timeout) {
        return setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * setIfAbsent
     */
    public <T> Boolean setIfAbsent(final String key, final T value) {
        return opsForValue().setIfAbsent(key, value);
    }

    /**
     * 执行 opsForList 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public ListOperations<String, Object> opsForList() {
        return redisTemplate.opsForList();
    }

    /**
     * 缓存List数据
     */
    @SafeVarargs
    public final <T> Long setCacheList(final String key, final T... datas) {
        return opsForList().rightPushAll(key, datas);
    }

    /**
     * 缓存List数据
     */
    public <T> Long setCacheList(final String key, final List<T> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            return 0L;
        }
        return setCacheList(key, dataList.toArray());
    }

    /**
     * 获得缓存List
     */
    public <T> List<T> getCacheList(final String key, final long start, long end) {
        return (List<T>) opsForList().range(key, start, end);
    }

    /**
     * 从列表左边弹出一个元素
     */
    public <T> T leftPop(final String key) {
        return (T) opsForList().leftPop(key);
    }

    /**
     * 左弹多个元素
     */
    public <T> List<T> leftPop(final String key, final long count) {
        return (List<T>) opsForList().leftPop(key, count <= 0 ? 1 : count);
    }

    /**
     * 右弹多个元素
     */
    public <T> List<T> rightPop(final String key, final long count) {
        return (List<T>) opsForList().rightPop(key, count <= 0 ? 1 : count);
    }

    /**
     * 右弹元素
     */
    public <T> T rightPop(final String key) {
        return (T) opsForList().rightPop(key);
    }

    /**
     * 获得缓存List
     */
    public <T> List<T> getCacheList(final String key) {
        return getCacheList(key, 0, -1);
    }

    /**
     * 执行 opsForZSet 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public ZSetOperations<String, Object> opsForZSet() {
        return redisTemplate.opsForZSet();
    }

    /**
     * 执行 reverseRangeByScoreWithScores 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key    key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param min    min 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param max    max 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param offset offset 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param count  count 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> Set<ZSetOperations.TypedTuple<T>> reverseRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        ZSetOperations<String, T> opZSet = (ZSetOperations<String, T>) redisTemplate.opsForZSet();
        return opZSet.reverseRangeByScoreWithScores(key, min, max, offset, count);
    }

    /**
     * 执行 rangeByScoreWithScores 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param min min 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param max max 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> Set<ZSetOperations.TypedTuple<T>> rangeByScoreWithScores(String key, double min, double max) {
        ZSetOperations<String, T> opZSet = (ZSetOperations<String, T>) redisTemplate.opsForZSet();
        return opZSet.rangeByScoreWithScores(key, min, max);
    }

    /**
     * 获得缓存的list对象
     */
    public <T> Set<T> getZSetCacheList(final String key, final long start, long end) {
        return (Set<T>) opsForZSet().range(key, start, end);
    }

    /**
     * ZSet获得缓存的list对象
     */
    public <T> Set<T> getZSetCacheList(final String key) {
        return getZSetCacheList(key, 0, -1);
    }

    /**
     * ZSet中移除元素
     */
    public <T> Long removeZSet(final String key, final Object... values) {
        return opsForZSet().remove(key, values);
    }

    /**
     * ZSet中移除元素
     */
    public <T> Long removeStrZSet(final String key, final Object... values) {
        Object[] arguments = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            arguments[i] = (values[i] instanceof String) ? (String) values[i] : String.valueOf(values[i]);
        }
        return removeZSet(key, arguments);
    }

    /**
     * incrementScore 增减有序集合中成员的分数
     */
    public <T> Double incrementScore(final String key, final T value, final double delta) {
        return opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 执行 opsForSet 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public SetOperations<String, Object> opsForSet() {
        return redisTemplate.opsForSet();
    }

    /**
     * 获得缓存的set
     */
    public <T> Set<T> getCacheSet(final String key) {
        return (Set<T>) opsForSet().members(key);
    }

    /**
     * 将数据放入set缓存
     */
    public <T> Long addSet(String key, T... values) {
        return opsForSet().add(key, values);
    }

    /**
     * 随机从set中取出一个元素
     */
    public <T> Object popSet(final String key) {
        return opsForSet().pop(key);
    }

    /**
     * set中移除元素
     */
    public <T> Long removeSet(final String key, final Object... values) {
        return opsForSet().remove(key, values);
    }

    /**
     * 读取 Member 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @param key   key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param value value 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Boolean isMember(final String key, final Object value) {
        return opsForSet().isMember(key, value);
    }

    /**
     * 执行 opsForHash 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public HashOperations<String, Object, Object> opsForHash() {
        return redisTemplate.opsForHash();
    }

    /**
     * 缓存Map
     */
    public <K, V> void hashPutAll(final String key, final Map<K, V> m) {
        if (m != null && !m.isEmpty()) {
            opsForHash().putAll(key, m);
        }
    }

    /**
     * 判断Hash里面有没有
     */
    public Boolean hasHashKey(final String key, final String hashKey) {
        return opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获得缓存的Map
     */
    public <T> List<T> getCacheValue(final String key) {
        return (List<T>) opsForHash().values(key);
    }

    /**
     * 获得缓存的Map
     */
    public <K, V> Map<K, V> hashEntries(final String key) {
        return (Map<K, V>) opsForHash().entries(key);
    }

    /**
     * 扫描
     */
    public <K, V> void hashScan(String hashKey, String matchPattern, long count, Consumer<Map.Entry<K, V>> consumer) {
        ScanOptions options = ScanOptions.scanOptions()
                .count(count)
                .match(StrUtil.blankToDefault(matchPattern, "*"))
                .build();

        Cursor<Map.Entry<Object, Object>> cursor = opsForHash().scan(hashKey, options);
        while (cursor.hasNext()) {
            consumer.accept((Map.Entry<K, V>) cursor.next());
        }
        cursor.close();
    }

    /**
     * 执行 hashScan 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param hashKey  hashKey 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param consumer consumer 参数，调用方应传入与 缓存能力 场景匹配的有效值
     */
    public <K, V> void hashScan(String hashKey, Consumer<Map.Entry<K, V>> consumer) {
        hashScan(hashKey, null, HALF_BATCH_SIZE, consumer);
    }

    /**
     * 扫描
     */
    public <V> Map<String, V> hashScan(String hashKey, String matchPattern, long count) {
        Map<String, V> map = new HashMap<>();
        hashScan(hashKey, matchPattern, count, entry -> map.put(Convert.toStr(entry.getKey()), (V) entry.getValue()));
        return map;
    }

    /**
     * 执行 hashScan 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param hashKey hashKey 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <V> Map<String, V> hashScan(String hashKey) {
        return hashScan(hashKey, null, HALF_BATCH_SIZE);
    }

    /**
     * 往Hash中存入数据
     */
    public <T> void hashPut(final String key, final String hashKey, final T value) {
        opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取Hash中的数据
     */
    public <T> T hashGet(final String key, final Object hashKey) {
        return (T) opsForHash().get(key, hashKey);
    }

    /**
     * 删除Hash中的某条数据
     */
    public Long hashDel(final String key, final Object... hashKeys) {
        return opsForHash().delete(key, hashKeys);
    }

    /**
     * 执行 hashDel 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key      key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param hashKeys hashKeys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <T> Long hashDel(final String key, final Collection<T> hashKeys) {
        return opsForHash().delete(key, hashKeys.toArray());
    }

    /**
     * 获取多个Hash中的数据
     */
    public <HK, HV> List<HV> hashMultiGet(final String key, final Collection<HK> hashKeys) {
        if (CollectionUtil.isNotEmpty(hashKeys)) {
            HashOperations<String, HK, HV> operations = redisTemplate.opsForHash();
            List<HV> lst = operations.multiGet(key, hashKeys);
            if (CollectionUtil.isNotEmpty(lst)) {
                // TODO 此处会有All elements are null 的情况
                return lst.stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * 获取多个Hash中的数据
     */
    public <HK, HV> List<HV> batchHashMultiGet(final String key, final Collection<HK> hashKeys, int batchSize) {
        if (hashKeys != null && hashKeys.size() > batchSize) {
            List<HV> hvList = new ArrayList<>();
            Lists.partition(new ArrayList<>(hashKeys), batchSize).forEach(lst -> hvList.addAll(hashMultiGet(key, lst)));
            return hvList;
        }
        return hashMultiGet(key, hashKeys);
    }

    /**
     * 执行 hasMultiKey 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key      key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param hashKeys hashKeys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <V, HK, HV> List<Boolean> hasMultiKey(final String key, final Collection<V> hashKeys) {
        return executePipelined2(redisOp -> {
            HashOperations<String, HK, HV> hashOp = (HashOperations<String, HK, HV>) redisOp.opsForHash();
            hashKeys.forEach(hashKey -> hashOp.hasKey(key, hashKey));
        });
    }

    /**
     * 执行 multiGetSet 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param keys keys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <K, V> List<V> multiGetSet(final Collection<K> keys) {
        return executePipelined2(redisOp -> {
            SetOperations<K, V> hashOp = (SetOperations<K, V>) redisOp.opsForSet();
            keys.forEach(hashOp::members);
        });
    }

    /**
     * 执行 multiGet 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param keys keys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <K, V> List<V> multiGet(final Collection<K> keys) {
        ValueOperations<K, V> op = (ValueOperations<K, V>) redisTemplate.opsForValue();
        return op.multiGet(keys);
    }

    /**
     * 执行 batchHasMultiKey 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key       key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param hashKeys  hashKeys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param batchSize batchSize 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <V> List<Boolean> batchHasMultiKey(final String key, final Collection<V> hashKeys, int batchSize) {
        if (hashKeys != null && hashKeys.size() > batchSize) {
            List<Boolean> resultList = new ArrayList<>();
            Lists.partition(new ArrayList<>(hashKeys), batchSize).forEach(lst -> resultList.addAll(hasMultiKey(key, lst)));
            return resultList;
        }
        return hasMultiKey(key, hashKeys);
    }

    /**
     * 执行 batchHasMultiKeyMap 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key      key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param hashKeys hashKeys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <V> Map<V, Boolean> batchHasMultiKeyMap(final String key, final Collection<V> hashKeys) {
        List<V> hKeys = new ArrayList<>(hashKeys);
        List<Boolean> results = hasMultiKey(key, hKeys);
        Map<V, Boolean> map = new HashMap<>();
        IntStream.range(0, hKeys.size()).forEach(i -> map.put(hKeys.get(i), results.get(i)));
        return map;
    }

    /**
     * 执行 batchHasMultiKeyMap 逻辑。
     * 该方法属于 缓存能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param key       key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param hashKeys  hashKeys 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param batchSize batchSize 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public <V> Map<V, Boolean> batchHasMultiKeyMap(final String key, final Collection<V> hashKeys, int batchSize) {
        if (hashKeys != null && hashKeys.size() > batchSize) {
            Map<V, Boolean> map = new HashMap<>();
            Lists.partition(new ArrayList<>(hashKeys), batchSize).forEach(lst -> map.putAll(batchHasMultiKeyMap(key, lst)));
            return map;
        }
        return batchHasMultiKeyMap(key, hashKeys);
    }

    /**
     * 获取list缓存的长度
     */
    public Long getListSize(final String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * scan遍历
     */
    public void scan(String prefix, int count, Consumer<String> action) {
        ScanOptions options = ScanOptions.scanOptions().match(prefix + "*").count(count).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(action);
        }
    }

    /**
     * 设置 Is Member 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param key     key 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @param members members 参数，调用方应传入与 缓存能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @SuppressWarnings("rawtypes")
    public <M> Map<M, Boolean> setIsMember(String key, List<M> members) {
        if (key == null || members == null || members.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object> retList = executePipelined2(connection -> {
            SetOperations operations = connection.opsForSet();
            members.forEach(member -> operations.isMember(key, member));
        });

        Map<M, Boolean> retMap = new HashMap<>();

        for (int i = 0; i < members.size(); i++) {
            retMap.put(members.get(i), Convert.toBool(retList.get(i)));
        }

        return retMap;
    }

    /**
     * Redis 7.4+: 支持 Hash Field 级别的 TTL
     * HEXPIRE
     */
    @SuppressWarnings("rawtypes")
    public Boolean hexpire(String key, String field, Long seconds) {

        // HEXPIRE "TEST_HASH:1917505652013240321" 166 FIELDS 1 "1918890174738964481"

        String lua = """
                return redis.call("HEXPIRE", KEYS[1], ARGV[1], "FIELDS", 1, "{}");
                """;

        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(StrUtil.format(lua, field));
        script.setResultType(List.class);

        try {
            List<?> result = redisTemplate.execute(script, Collections.singletonList(key), seconds);

            return Convert.toLong(result.get(0), 0L) == 1L;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Redis 7.4+: 支持 Hash Field 级别的 TTL
     * HPTTL (单位ms) TTL: 2999920 ms
     */
    @SuppressWarnings("rawtypes")
    public Long hpttl(String key, String field) {
        String lua = """
                return redis.call("HPTTL", KEYS[1], "FIELDS", 1, "{}");
                """;

        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(StrUtil.format(lua, field));
        script.setResultType(List.class);

        try {
            List<?> result = redisTemplate.execute(script, Collections.singletonList(key));

            return Convert.toLong(result.get(0), 0L);
        } catch (Exception ignored) {
            return -1L;
        }
    }

    /**
     * Redis 7.4+: 支持 Hash Field 级别的 TTL
     * HPERSIST
     */
    @SuppressWarnings("rawtypes")
    public Boolean hpersist(String key, String field) {
        String lua = """
                return redis.call("HPERSIST", KEYS[1], "FIELDS", 1, "{}");
                """;

        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(StrUtil.format(lua, field));
        script.setResultType(List.class);

        try {
            List<?> result = redisTemplate.execute(script, Collections.singletonList(key));

            return Convert.toLong(result.get(0), 0L) == 1L;
        } catch (Exception ignored) {
            return false;
        }
    }


}
