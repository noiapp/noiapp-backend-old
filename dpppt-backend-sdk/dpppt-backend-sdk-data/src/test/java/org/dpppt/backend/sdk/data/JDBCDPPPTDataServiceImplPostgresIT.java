package org.dpppt.backend.sdk.data;

import org.dpppt.backend.sdk.data.util.DBContainerJUnitExtension;
import org.dpppt.backend.sdk.data.util.SingletonPostgresContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

@ExtendWith(DBContainerJUnitExtension.class)
class JDBCDPPPTDataServiceImplPostgresIT extends AbstractJDBCDPPPTDataServiceImplIT {

    protected String getMigrationPath() {
        return "classpath:/db/migration/pgsql";
    }

    protected String getDbType() {
        return "pgsql";
    }

    protected DataSource getDataSource() {
        final SingletonPostgresContainer instance = SingletonPostgresContainer.getInstance();

        return DataSourceBuilder.create()
            .driverClassName(instance.getDriverClassName())
            .url(instance.getJdbcUrl())
            .username(instance.getUsername())
            .password(instance.getPassword())
            .build();
    }

}
