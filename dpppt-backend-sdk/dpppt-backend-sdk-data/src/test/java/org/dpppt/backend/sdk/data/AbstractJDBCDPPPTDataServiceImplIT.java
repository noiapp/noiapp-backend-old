package org.dpppt.backend.sdk.data;

import org.assertj.core.api.Assertions;
import org.dpppt.backend.sdk.model.Exposee;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public abstract class AbstractJDBCDPPPTDataServiceImplIT {

    private DPPPTDataService target;
    private DataSource dataSource;

    protected abstract String getMigrationPath();

    protected abstract String getDbType();

    protected abstract DataSource getDataSource();

    @BeforeEach
    void setUp() {

        dataSource = getDataSource();

        target = new JDBCDPPPTDataServiceImpl(getDbType(), dataSource);

        Flyway flyWay = Flyway.configure().dataSource(dataSource).locations(getMigrationPath()).load();
        flyWay.migrate();
    }

    @AfterEach
    void tearDown() throws SQLException {
        final String sql = "truncate table t_exposed";
        executeSQL(sql);
    }

    @Test
    void shouldAddAnExposee() throws SQLException {
        // GIVEN
        final long exposeeCountBefore = getExposeeCount();

        Exposee exposee = new Exposee();
        exposee.setKey("key1");
        exposee.setOnset("2014-01-28");

        // WHEN
        target.upsertExposee(exposee, "test-app");

        // THEN
        final long exposeeCountAfter = getExposeeCount();
        Assertions.assertThat(exposeeCountAfter).isEqualTo(exposeeCountBefore + 1);

        try (
            final Connection connection = dataSource.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("select * from t_exposed t where t.key = 'key1'");
            final ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();

            Assertions.assertThat(resultSet.getInt("pk_exposed_id")).isPositive();
            Assertions.assertThat(resultSet.getString("key")).isEqualTo("key1");
            Assertions.assertThat(resultSet.getString("received_at")).isNotNull();
            Assertions.assertThat(resultSet.getString("app_source")).isEqualTo("test-app");
            Assertions.assertThat(resultSet.getDate("onset")).isEqualTo(Date.valueOf(LocalDate.of(2014, 1, 28)));
        }
    }

    @Test
    void shouldUpdateExposee() throws SQLException {
        // GIVEN

        Exposee exposee = new Exposee();
        exposee.setKey("key1");
        exposee.setOnset("2014-01-28");

        target.upsertExposee(exposee, "test-app");

        // WHEN update
        final long exposeeCountBefore = getExposeeCount();

        exposee.setKey("key1");
        exposee.setOnset("2014-02-28");

        target.upsertExposee(exposee, "test-app-fix");

        // THEN
        final long exposeeCountAfter = getExposeeCount();
        Assertions.assertThat(exposeeCountAfter).isEqualTo(exposeeCountBefore);

        try (
            final Connection connection = dataSource.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("select * from t_exposed t where t.key = 'key1'");
            final ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();

            Assertions.assertThat(resultSet.getInt("pk_exposed_id")).isPositive();
            Assertions.assertThat(resultSet.getString("key")).isEqualTo("key1");
            Assertions.assertThat(resultSet.getString("received_at")).isNotNull();
            Assertions.assertThat(resultSet.getDate("onset")).isEqualTo(Date.valueOf(LocalDate.of(2014, 2, 28)));
            Assertions.assertThat(resultSet.getString("app_source")).isEqualTo("test-app-fix");
        }
    }

    @Test
    void shouldGetSortedExposedForDay() {

        // GIVEN
        {
            Exposee exposee = new Exposee();
            exposee.setKey("key1");
            exposee.setOnset("2014-01-28");

            target.upsertExposee(exposee, "test-app");
        }

        {
            Exposee exposee = new Exposee();
            exposee.setKey("key2");
            exposee.setOnset("2014-01-29");

            target.upsertExposee(exposee, "test-app");
        }

        // WHEN
        final List<Exposee> sortedExposedForDay = target.getSortedExposedForDay(LocalDate.now());

        // THEN
        Assertions.assertThat(sortedExposedForDay).hasSize(2);
        Assertions.assertThat(sortedExposedForDay.get(0).getKey()).isEqualTo("key2");
        Assertions.assertThat(sortedExposedForDay.get(1).getKey()).isEqualTo("key1");
    }

    @Test
    void shouldReturnEmptyListForGetSortedExposedForDay() {

        // WHEN
        final List<Exposee> sortedExposedForDay = target.getSortedExposedForDay(LocalDate.now());

        // THEN
        Assertions.assertThat(sortedExposedForDay).isEmpty();
    }

    @Test
    void shouldGetMaxExposedIdForDay() throws SQLException {

        // GIVEN
        {
            Exposee exposee = new Exposee();
            exposee.setKey("key1");
            exposee.setOnset("2014-01-28");

            target.upsertExposee(exposee, "test-app");
        }

        {
            Exposee exposee = new Exposee();
            exposee.setKey("key2");
            exposee.setOnset("2014-01-29");

            target.upsertExposee(exposee, "test-app");
        }

        // WHEN
        final Integer maxExposedIdForDay = target.getMaxExposedIdForDay(LocalDate.now());

        // THEN
        try (
            final Connection connection = dataSource.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("select * from t_exposed t where t.key = 'key2'");
            final ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();

            Assertions.assertThat(maxExposedIdForDay).isEqualTo(resultSet.getInt("pk_exposed_id"));
        }
    }

    @Test
    void shouldGetZeroForGetMaxExposedIdForDay() {

        // WHEN
        final Integer maxExposedIdForDay = target.getMaxExposedIdForDay(LocalDate.now());

        // THEN
        Assertions.assertThat(maxExposedIdForDay).isEqualTo(0);
    }

    private long getExposeeCount() throws SQLException {
        try (
            final Connection connection = dataSource.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("select count(*) from t_exposed");
            final ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    protected void executeSQL(String sql) throws SQLException {
        try (
            final Connection connection = dataSource.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.execute();
        }
    }
}
