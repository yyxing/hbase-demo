package com.devil.core.util;


import com.devil.common.util.YamlUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Devil
 * @version 1.0
 * @date 2020/6/6 10:48
 * @Desc Hase配置连接类
 */
@Slf4j
public class HBaseConnection {

    /**
     * 线程池大小 默认20
     */
    private Integer poolSize = 20;
    /**
     * 线程池类型 默认default
     */
    private String poolType = "default";
    /**
     * zookeeper地址 默认localhost
     */
    private String zookeeperAddress = "localhost";
    /**
     * zookeeper 端口 默认2181
     */
    private Integer zookeeperPort = 2181;

    private List<String> configPrefixList = Arrays.asList("hdfs.pool.size", "hdfs.pool.type",
            "hdfs.zookeeper.address", "hdfs.zookeeper.port");
    private static HBaseConnection instance;
    private static Configuration configuration;
    private static Connection connection;
    private static ExecutorService executor;

    /**
     * 读取properties中的配置完成初始化
     */
    private void configurationInit() {
        // 构建ResourceLoader
        Map<String, Object> properties;
        properties = YamlUtil.getPropertiesByYaml(YamlUtil.DEFAULT_YAML);
//        if (ObjectUtils.isEmpty(properties)) {
//            properties = YamlUtil.getPropertiesByYaml(YamlUtil.DEFAULT_BOOTSTRAP_YAML);
//        }
//        // 判断配置是否为空
//        for (int i = 0 ; i < configPrefixList.size() ; i++) {
//            String config = configPrefixList.get(i);
//            if (!properties.containsKey(config)) {
//                throw new HBaseOperationException(String.format("%s配置不能为空", config));
//            }
//        }
        if (!properties.isEmpty()) {
            poolSize = (Integer) properties.getOrDefault(configPrefixList.get(0), poolSize);
            poolType = (String) properties.getOrDefault(configPrefixList.get(1), poolType);
            zookeeperAddress = (String) properties.getOrDefault(configPrefixList.get(2), zookeeperAddress);
            zookeeperPort = (Integer) properties.getOrDefault(configPrefixList.get(3), zookeeperPort);
        }

    }

    /**
     * 获取配置
     *
     * @return
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    // HBase数据库实例化
    private HBaseConnection() {
        try {
            configurationInit();
            // HBase 配置类
            configuration = HBaseConfiguration.create();
            // 设置每个Region Server处理的线程数量
            configuration.set("hbase.client.ipc.pool.size", String.valueOf(poolSize));
            // 设置HBase的Zookeeper地址
            configuration.set("hbase.zookeeper.quorum", zookeeperAddress);
            // 设置Zookeeper端口
            configuration.set("hbase.zookeeper.property.clientPort", String.valueOf(zookeeperPort));
            // 设置Executors 不同的线程中使用单独的Table和Admin对象
            executor = Executors.newFixedThreadPool(poolSize);
            // 创建HBase连接类
            connection = ConnectionFactory.createConnection(configuration, executor);
        } catch (Exception e) {
            log.error("初始化HBase连接时错误，错误信息为：", e);
        }
    }

    /**
     * 单例获取Connection对象
     *
     * @return
     */
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

    /**
     * 获取HBase连接类
     *
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getInstance().getConnection().getAdmin().getProcedures());
    }
}
