package com.yizlan.code.gen.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baomidou.mybatisplus.annotation.DbType;
import com.yizlan.code.gen.service.DruidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 数据库连接池 服务实现类
 *
 * @author Alias Zen
 * @version 1.0
 * @since 2022-11-30
 */
@Slf4j
@Service
public class DruidServiceImpl implements DruidService {

    private Map<String, DruidDataSource> map = new HashMap<>();

    public DruidDataSource initDataSource(String driver, String url, String userName, String password, String dbType) throws Exception {

        DruidDataSource source;
        synchronized (this) {
            if (!map.containsKey(url)) {
                Properties prop = new Properties();
                prop.setProperty(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, driver);
                prop.setProperty(DruidDataSourceFactory.PROP_URL, url);
                prop.setProperty(DruidDataSourceFactory.PROP_USERNAME, userName);
                prop.setProperty(DruidDataSourceFactory.PROP_PASSWORD, password);
                prop.setProperty(DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES, "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500;useUnicode=true;characterEncoding=UTF8");
                // 设置数据连接池初始化连接池数量
                prop.setProperty(DruidDataSourceFactory.PROP_INITIALSIZE, "5");
                // 最大、最小连接数
                prop.setProperty(DruidDataSourceFactory.PROP_MAXACTIVE, "20");
                prop.setProperty(DruidDataSourceFactory.PROP_MINIDLE, "5");
                // 连接最大等待时间60秒
                prop.setProperty(DruidDataSourceFactory.PROP_MAXWAIT, "60000");
                // 间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
                prop.setProperty(DruidDataSourceFactory.PROP_TIMEBETWEENEVICTIONRUNSMILLIS, "60000");
                // 一个连接在池中最小生存的时间
                prop.setProperty(DruidDataSourceFactory.PROP_MINEVICTABLEIDLETIMEMILLIS, "600000");

                prop.setProperty(DruidDataSourceFactory.PROP_VALIDATIONQUERY, DbType.ORACLE.getDb().equals(dbType) ? "select 1 FROM DUAL" : "select 1");
                prop.setProperty(DruidDataSourceFactory.PROP_TESTWHILEIDLE, "true");
                prop.setProperty(DruidDataSourceFactory.PROP_TESTONBORROW, "false");
                prop.setProperty(DruidDataSourceFactory.PROP_TESTONRETURN, "false");

                prop.setProperty(DruidDataSourceFactory.PROP_POOLPREPAREDSTATEMENTS, "false");
                prop.setProperty(DruidDataSourceFactory.PROP_MAXOPENPREPAREDSTATEMENTS, "20");
                // 监控统计拦截的filters，stat:监控统计、log4j：日志记录、wall：防御sql注入
                prop.setProperty(DruidDataSourceFactory.PROP_FILTERS, "stat,wall");

                source = (DruidDataSource) DruidDataSourceFactory.createDataSource(prop);
                map.put(url, source);
                return source;
            }
        }
        return map.get(url);
    }
}
