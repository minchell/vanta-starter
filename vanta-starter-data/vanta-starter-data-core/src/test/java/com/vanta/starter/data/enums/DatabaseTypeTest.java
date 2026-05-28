package com.vanta.starter.data.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseTypeTest {

    @Test
    void shouldUsePostgresqlAsCanonicalEnumNameAndKeepLegacyAliasCompatible() {
        assertThat(DatabaseType.get("PostgreSQL")).isSameAs(DatabaseType.POSTGRESQL);
        assertThat(DatabaseType.POSTGRE_SQL).isSameAs(DatabaseType.POSTGRESQL);
    }
}
