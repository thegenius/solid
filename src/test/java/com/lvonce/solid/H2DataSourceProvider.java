package com.lvonce.solid;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@SqlDataSource(env = "dev", key = "h2-mem")
public class H2DataSourceProvider implements Provider<DataSource> {
    private void init(DataSource dataSource) {
        try {
            Connection conn = dataSource.getConnection();
            String table = "CREATE TABLE if not exists person(\n" +
                    " id int not null,\n" +
                    " name varchar(20) null,\n" +
                    " age int unsigned null,\n" +
                    " primary key (id),\n" +
                    " key test_idx_name_age(name, age)\n" +
                    ");";


            PreparedStatement createStatement = conn.prepareStatement(table);
            createStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DataSource get() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:dbc2m;DATABASE_TO_UPPER=false;MODE=MYSQL");
        config.setUsername("sa");
        config.setPassword("");
        config.addDataSourceProperty("cachePrepStmts", true);
        DataSource dataSource = new HikariDataSource(config);
        init(dataSource);
        return dataSource;
    }
}
