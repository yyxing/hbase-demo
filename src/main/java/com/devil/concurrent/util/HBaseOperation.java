package com.devil.concurrent.util;

import com.devil.concurrent.exception.HBaseOperationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
     *
     * @param tableName 表名
     * @return {@link Table}
     */
    public Table getTable(String tableName) {
        Table table = null;
        try {
            table = connection.getTable(getTableName(tableName));
        } catch (IOException e) {
            log.error("获取表名为{}的Table失败，失败信息为：", tableName, e);
        }
        return table;
    }

    /**
     * 创建HBase表 创建HBase表时需要预先根据表模式设置列族的值，但是列族里面的值可以不设定
     *
     * @param tableName 表名
     * @param cfs       列族
     * @return 是否创建成功
     */
    public boolean createTable(String tableName, String... cfs) {
        try {
            // 获取HBase分布式节点的Admin(管理员)
            HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
            TableName table = getTableName(tableName);
            // 判断该表是否存在
            if (admin.tableExists(table)) {
                throw new HBaseOperationException(String.format("表名为%s的表已存在，请先删除后创建。", tableName));
            }
            // HBase表描述符 获取HBase表的一些信息
            // hadoop在2.x的时候将这个方法弃用 使用TableDescriptorBuilder构造Builder后获取TableDescriptor
            // HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tableName));
            TableDescriptorBuilder descriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
            // 列族描述符 用来创建表或者添加列族 在之前版本使用descriptor.addFamily()
            List<ColumnFamilyDescriptor> columnFamilies = Arrays.stream(cfs).map(ColumnFamilyDescriptorBuilder::of).collect(Collectors.toList());
            // 根据建造者模式创建以及执行对Table的相关操作，例如增删改查
            TableDescriptor descriptor = descriptorBuilder.setColumnFamilies(columnFamilies).build();
            // 执行添加表操作 利用RPC远程execute
            admin.createTable(descriptor);
        } catch (IOException e) {
            log.error("创建表名为{}的Table失败，失败信息为：", tableName, e);
            return false;
        }
        return true;
    }

    /**
     * 删除HBase表
     *
     * @param tableName 表名
     * @return 是否删除成功
     */
    public boolean deleteTable(String tableName) {
        try {
            // 获取HBase分布式节点的Admin(管理员)
            HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
            TableName table = getTableName(tableName);
            // 判断该表是否存在
            if (!admin.tableExists(table)) {
                return true;
            }
            admin.disableTable(table);
            // 执行删除表操作 利用RPC远程execute
            admin.deleteTable(table);
        } catch (IOException e) {
            log.error("删除表名为{}的Table失败，失败信息为：", tableName, e);
            return false;
        }
        return true;
    }

    /**
     * 对某张表增加列族
     *
     * @param tableName    表名
     * @param columnFamily 要增加的列族名
     * @return 是否添加成功
     */
    public boolean addColumnFamily(String tableName, String columnFamily) {
        try {
            // 获取HBase分布式节点的Admin(管理员)
            HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
            TableName table = getTableName(tableName);
            // 判断该表是否存在
            if (!admin.tableExists(table)) {
                throw new HBaseOperationException(String.format("表名为%s的表已存在，无法添加列族。", tableName));
            }
            // 对要添加的列名构造描述符
            ColumnFamilyDescriptor familyDescriptor = ColumnFamilyDescriptorBuilder.of(columnFamily);
            // 执行添加列族操作 利用RPC远程execute
            admin.addColumnFamily(table, familyDescriptor);
        } catch (IOException e) {
            log.error("对表名为{}的Table添加列族失败，失败信息为：", tableName, e);
            return false;
        }
        return true;
    }

    /**
     * 向指定表的指定列族添加数据
     *
     * @param tableName    表名
     * @param columnFamily 列族名
     * @param rowKey       唯一标识
     * @param key          列标识 也就是列族:列标识 对应数据
     * @param value        数据
     * @return 是否插入成功
     */
    public boolean putRow(String tableName, String columnFamily, String rowKey, String key, String value) {
        try {
            // 获取HBase表类，可利用这个类对表进行操作
            Table table = getTable(tableName);
            // 判断该表是否存在
            if (ObjectUtils.isEmpty(table)) {
                throw new HBaseOperationException(String.format("表名为%s的表已存在，无法添加行。", tableName));
            }
            // 构建添加数据所需要的Put类
            Put put = new Put(rowKey.getBytes());
            // 构建一行数据
            put.addColumn(columnFamily.getBytes(), key.getBytes(), value.getBytes());
            // 使用table的put完成对某个HBase表的插入操作
            table.put(put);
        } catch (IOException e) {
            log.error("对表名为{}，列族为{}的Table添加失败，失败信息为：", tableName, columnFamily, e);
            return false;
        }
        return true;
    }

    /**
     * 获取HBase表名对象
     *
     * @param tableName 表名字符串
     * @return {@link TableName}
     */
    public TableName getTableName(String tableName) {
        return TableName.valueOf(tableName);
    }

}
