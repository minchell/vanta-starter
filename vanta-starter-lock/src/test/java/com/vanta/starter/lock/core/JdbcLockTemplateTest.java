package com.vanta.starter.lock.core;

import com.vanta.starter.lock.autoconfigure.LockProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcLockTemplateTest {

    private JdbcTemplate jdbcTemplate;
    private JdbcLockTemplate lockTemplate;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:vanta_lock_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DROP TABLE IF EXISTS vanta_lock");
        jdbcTemplate.execute("""
                CREATE TABLE vanta_lock (
                    lock_key VARCHAR(180) PRIMARY KEY,
                    lock_token VARCHAR(64) NOT NULL,
                    expire_at TIMESTAMP NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL
                )
                """);
        LockProperties properties = new LockProperties();
        properties.setLeaseTime(Duration.ofSeconds(3));
        properties.setJdbcTableName("vanta_lock");
        lockTemplate = new JdbcLockTemplate(jdbcTemplate, properties);
    }

    @Test
    void shouldExecuteCallbackAndReleaseRowWhenJdbcLockAcquired() {
        LockExecutionResult<String> result = lockTemplate.execute("order:1", Duration.ZERO, () -> "ok");

        Integer rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vanta_lock", Integer.class);
        assertThat(result.acquired()).isTrue();
        assertThat(result.value()).isEqualTo("ok");
        assertThat(result.error()).isNull();
        assertThat(rowCount).isZero();
    }

    @Test
    void shouldReturnNotAcquiredWhenActiveJdbcLockExists() {
        insertLock("order:2", Instant.now().plusSeconds(30));
        AtomicInteger callbackCount = new AtomicInteger();

        LockExecutionResult<String> result = lockTemplate.execute("order:2", Duration.ZERO, () -> {
            callbackCount.incrementAndGet();
            return "unexpected";
        });

        assertThat(result.acquired()).isFalse();
        assertThat(result.value()).isNull();
        assertThat(result.error()).isNull();
        assertThat(callbackCount).hasValue(0);
    }

    @Test
    void shouldTakeOverExpiredJdbcLock() {
        insertLock("order:3", Instant.now().minusSeconds(30));

        LockExecutionResult<String> result = lockTemplate.execute("order:3", Duration.ZERO, () -> "recovered");

        Integer rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vanta_lock", Integer.class);
        assertThat(result.acquired()).isTrue();
        assertThat(result.value()).isEqualTo("recovered");
        assertThat(rowCount).isZero();
    }

    private void insertLock(String key, Instant expireAt) {
        Timestamp now = Timestamp.from(Instant.now());
        jdbcTemplate.update("""
                        INSERT INTO vanta_lock (lock_key, lock_token, expire_at, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?)
                        """,
                key,
                "existing-token",
                Timestamp.from(expireAt),
                now,
                now);
    }
}
