package com.devil.storage.config;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.servlet.resource.PathResourceResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Phoenix数据源配置类
 *
 * @author Devil
 * @version 1.0
 * @date 2020/6/3 11:56
 */
@Configuration
@MapperScan(basePackages = PhoenixDatasourceConfig.PACKAGE,
        sqlSessionFactoryRef = PhoenixDatasourceConfig.PHOENIX_SQL_SESSION_FACTORY)
public class PhoenixDatasourceConfig {

    // 实体类所在包
    static final String PACKAGE = "com.devil.storage.entity";
    // SqlSessionFactoryBean名称
    static final String PHOENIX_SQL_SESSION_FACTORY = "PhoenixSqlSessionFactory";
    //Mapper文件路径
    static final String MAPPER_LOCATION = "classpath:mapper/*.xml";
    // 数据源Bean名称
    static final String DATASOURCE_NAME = "PhoenixDataSource";

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;


    @Bean(PhoenixDatasourceConfig.DATASOURCE_NAME)
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }


    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier(PhoenixDatasourceConfig.DATASOURCE_NAME)
                                                                   DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 构造数据库会话工厂，当使用数据的时候
     * @param dataSource
     * @return
     * @throws IOException
     */
    @Bean
    public SqlSessionFactoryBean sessionFactoryBean(@Qualifier(PhoenixDatasourceConfig.DATASOURCE_NAME) DataSource dataSource) throws IOException {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources(PhoenixDatasourceConfig.MAPPER_LOCATION));
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }
}
