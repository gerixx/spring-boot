package com.smec.cc.accountsmanagement.db.entity;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class JdbcUtil {

    public static void showTables(DataSource dataSource, Logger logger) throws Exception {
        DatabaseMetaData metaData = dataSource.getConnection()
                                              .getMetaData();
        ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            String tableSchem = tables.getString("TABLE_SCHEM");
            logger.info("{}.{}", tableSchem, tableName);
            ResultSet columns = metaData.getColumns(null, tableSchem, tableName, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                logger.info("\t" + columnName);
            }
        }
    }
}
