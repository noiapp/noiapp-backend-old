/*
 * Created by Ubique Innovation AG
 * https://www.ubique.ch
 * Copyright (c) 2020. All rights reserved.
 */

package org.dpppt.backend.sdk.data;

import org.dpppt.backend.sdk.model.Exposee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class JDBCDPPPTDataServiceImpl implements DPPPTDataService {

    private static final Logger logger = LoggerFactory.getLogger(JDBCDPPPTDataServiceImpl.class);
    private static final String PGSQL = "pgsql";

    // FIXME MAKE ENUMERATION
    private final String dbType;
    private final NamedParameterJdbcTemplate jt;

    public JDBCDPPPTDataServiceImpl(String dbType, DataSource dataSource) {
        this.dbType = dbType;
        this.jt = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    @Transactional
    public void upsertExposee(Exposee exposee, String appSource) {
        String sql;
        if (dbType.equals(PGSQL)) {
            sql = "insert into t_exposed (key, onset, app_source) values (:key, to_date(:onset, 'yyyy-MM-dd'), :app_source)"
                + " on conflict on constraint key do update set onset = excluded.onset, app_source = excluded.app_source";
        } else {
            sql = "merge into t_exposed using (values(cast(:key as varchar(10000)), cast(:onset as date), cast(:app_source as varchar(50))))"
                + " as vals(key, onset, app_source) on t_exposed.key = vals.key"
                + " when matched then update set onset = vals.onset, app_source = vals.app_source"
                + " when not matched then insert (key, onset, app_source) values (vals.key, vals.onset, vals.app_source)";
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("key", exposee.getKey());
        params.addValue("app_source", appSource);
        params.addValue("onset", exposee.getOnset());
        jt.update(sql, params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exposee> getSortedExposedForDay(LocalDate day) {
        LocalDateTime dayMidnight = day.atStartOfDay();
        String sql = "select pk_exposed_id, key, to_char(onset, 'yyyy-MM-dd') as onset_string from t_exposed where received_at >= :dayMidnight and received_at < :nextDayMidnight order by pk_exposed_id desc";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("dayMidnight", dayMidnight);
        params.addValue("nextDayMidnight", dayMidnight.plusDays(1));
        return jt.query(sql, params, new ExposeeRowMapper());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getMaxExposedIdForDay(LocalDate day) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        LocalDateTime dayMidnight = day.atStartOfDay();
        params.addValue("dayMidnight", dayMidnight);
        params.addValue("nextDayMidnight", dayMidnight.plusDays(1));
        String sql = "select max(pk_exposed_id) from t_exposed where received_at >= :dayMidnight and received_at < :nextDayMidnight";
        // FIXME USE LONG?
        Integer maxId = jt.queryForObject(sql, params, Integer.class);
        if (maxId == null) {
            return 0;
        } else {
            return maxId;
        }
    }
}
