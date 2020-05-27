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
public class HBaseConnection {

    @Value("hbase.pool.size")
    private String poolSize;
    @Value("hbase.pool.type")
    private String poolType;
    @Value("hbase.zookeeper.address")
    private String zookeeperAddress;
    @Value("hbase.zookeeper.address")
    private String zookeeperPort;

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
            configuration.set("hbase.client.ipc.pool.size", "20");
            // 设置HBase的Zookeeper地址
            configuration.set("hbase.zookeeper.quorum", "192.168.253.129");
            configuration.set("hbase.zookeeper.property.clientPort", "2181");
            configuration.set("fs.defaultFs", "192.168.253.129:9000");
            // 设置Executors 不同的线程中使用单独的Table和Admin对象
            executor = Executors.newFixedThreadPool(20);
            // 创建HBase连接类
            connection = ConnectionFactory.createConnection(configuration, executor);
        } catch (Exception e) {
            log.error("初始化HBase连接时错误");
        }
    }

    public static HBaseConnection getInstance() {
        if (instance == null) {
            synchronized (HBaseConnection.class) {
                if (instance == null) {
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
