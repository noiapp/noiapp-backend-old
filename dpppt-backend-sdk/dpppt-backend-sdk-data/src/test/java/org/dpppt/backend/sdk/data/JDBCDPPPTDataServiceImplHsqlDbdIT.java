package org.dpppt.backend.sdk.data;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

class JDBCDPPPTDataServiceImplHsqlDbdIT extends AbstractJDBCDPPPTDataServiceImplIT {

    @Override
    protected String getMigrationPath() {
        return "classpath:/db/migration/hsqldb";
    }

    @Override
    protected String getDbType() {
        return "hsqldb";
    }

    @Override
    protected DataSource getDataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
    }
}
