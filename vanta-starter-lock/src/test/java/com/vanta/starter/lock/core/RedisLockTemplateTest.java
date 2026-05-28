package com.vanta.starter.lock.core;

import com.vanta.starter.lock.autoconfigure.LockProperties;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisLockTemplateTest {

    @Test
    void shouldExecuteCallbackAndUnlockWhenRedisLockAcquired() throws Exception {
        RedissonClient redissonClient = mock(RedissonClient.class);
        RLock lock = mock(RLock.class);
        when(redissonClient.getLock("vanta:lock:demo")).thenReturn(lock);
        when(lock.tryLock(25, 3000, TimeUnit.MILLISECONDS)).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        RedisLockTemplate template = new RedisLockTemplate(redissonClient, properties());

        LockExecutionResult<String> result = template.execute("demo", Duration.ofMillis(25), () -> "ok");

        assertThat(result.acquired()).isTrue();
        assertThat(result.value()).isEqualTo("ok");
        assertThat(result.error()).isNull();
        verify(lock).unlock();
    }

    @Test
    void shouldNotExecuteCallbackWhenRedisLockIsNotAcquired() throws Exception {
        RedissonClient redissonClient = mock(RedissonClient.class);
        RLock lock = mock(RLock.class);
        when(redissonClient.getLock("vanta:lock:busy")).thenReturn(lock);
        when(lock.tryLock(10, 3000, TimeUnit.MILLISECONDS)).thenReturn(false);
        RedisLockTemplate template = new RedisLockTemplate(redissonClient, properties());
        AtomicInteger callbackCount = new AtomicInteger();

        LockExecutionResult<String> result = template.execute("busy", Duration.ofMillis(10), () -> {
            callbackCount.incrementAndGet();
            return "unexpected";
        });

        assertThat(result.acquired()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.error()).isNull();
        assertThat(callbackCount).hasValue(0);
        verify(lock, never()).unlock();
    }

    private LockProperties properties() {
        LockProperties properties = new LockProperties();
        properties.setRedisKeyPrefix("vanta:lock:");
        properties.setLeaseTime(Duration.ofSeconds(3));
        return properties;
    }
}
