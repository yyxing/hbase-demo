package com.devil.concurrent.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author Devil
 * @version 1.0
 * @date 2020/5/26 18:10
 */

@Data
@Component
@Slf4j
public class HBaseOperation {

    // HBase 连接类
    private final static Connection connection = HBaseConnection.getInstance().getConnection();

    /**
     * 根据表名获取HBase中的Table
     * @param tableName 表名
     * @return @link (Table)
     */
    public Table getTable(String tableName) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            log.error("获取Table名称为{}的Table失败，失败信息为：", tableName, e);
        }
        return table;
    }
}
