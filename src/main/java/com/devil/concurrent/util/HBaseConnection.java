package com.devil.concurrent.util;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Data
@Slf4j
@Component
public class HBaseConnection {

    @Value("hbase.pool.size")
    private String poolSize;
    @Value("hbase.pool.type")
    private String poolType;
    @Value("hbase.zookeeper.address")
    private String zookeeperAddress;
    @Value("hbase.zookeeper.address")
    private Integer zookeeperPort;

    private static HBaseConnection instance;
    private static Configuration configuration;
    private static Connection connection;
    private static ExecutorService executor;

    // HBase数据库实例化
    private HBaseConnection() {
        try {
            // HBase 配置类
            configuration = HBaseConfiguration.create();
            // 设置每个Region Server处理的线程数量
            configuration.set("hbase.client.ipc.pool.size", poolSize);
            // 设置HBase的Zookeeper地址
            String zookeeperUrl = String.format("%s:%d", zookeeperAddress, zookeeperPort);
            configuration.set("hbase.zookeeper.quorum", zookeeperUrl);
            // 设置Executors 不同的线程中使用单独的Table和Admin对象
            executor = Executors.newFixedThreadPool(Integer.parseInt(poolSize));
            // 创建HBase连接类
            connection = ConnectionFactory.createConnection(configuration, executor);
        } catch (Exception e) {
            log.error("初始化HBase连接时错误");
        }
    }

    public static HBaseConnection getInstance() {
        if (connection == null) {
            synchronized (HBaseConnection.class) {
                if (connection == null) {
                    instance = new HBaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection(){
        return connection;
    }
}
