package com.aliaszen.code.gen.service.impl;

import com.aliaszen.code.gen.service.DruidService;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
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

    @Override
    public DruidPooledConnection getOrCreateConnection(String driver, String url, String userName,
                                                       String password) throws Exception {
        DruidDataSource source;
        synchronized (this) {
            if (!map.containsKey(url)) {
                source = initDataSource(driver, url, userName, password);
                return (DruidPooledConnection) source.getPooledConnection();
            }
        }
        source = map.get(url);
        log.debug("当前数据库连接池的量为：" + source.getActiveConnections().size() + "---" + source.getActiveCount() + "---" + source.getCloseCount());
        return (DruidPooledConnection) source.getPooledConnection();
    }

    public DruidDataSource initDataSource(String driver, String url, String userName, String password) throws Exception {
        Properties prop = new Properties();
        prop.setProperty(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, driver);
        prop.setProperty(DruidDataSourceFactory.PROP_URL, url);
        prop.setProperty(DruidDataSourceFactory.PROP_USERNAME, userName);
        prop.setProperty(DruidDataSourceFactory.PROP_PASSWORD, password);
        prop.setProperty(DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES, "useUnicode=true;characterEncoding=UTF8");
        // 设置数据连接池初始化连接池数量
        prop.setProperty(DruidDataSourceFactory.PROP_INITIALSIZE, "5");
        // 最大、最小连接数
        prop.setProperty(DruidDataSourceFactory.PROP_MAXACTIVE, "50");
        prop.setProperty(DruidDataSourceFactory.PROP_MINIDLE, "3");
        // 连接最大等待时间60秒
        prop.setProperty(DruidDataSourceFactory.PROP_MAXWAIT, "60000");
        // 间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        prop.setProperty(DruidDataSourceFactory.PROP_TIMEBETWEENEVICTIONRUNSMILLIS, "35000");
        // 一个连接在池中最小生存的时间
        prop.setProperty(DruidDataSourceFactory.PROP_MINEVICTABLEIDLETIMEMILLIS, "600000");

        prop.setProperty(DruidDataSourceFactory.PROP_VALIDATIONQUERY, "select 1");
        prop.setProperty(DruidDataSourceFactory.PROP_TESTWHILEIDLE, "true");
        prop.setProperty(DruidDataSourceFactory.PROP_TESTONBORROW, "false");
        prop.setProperty(DruidDataSourceFactory.PROP_TESTONRETURN, "false");

        prop.setProperty(DruidDataSourceFactory.PROP_POOLPREPAREDSTATEMENTS, "false");
        prop.setProperty(DruidDataSourceFactory.PROP_MAXOPENPREPAREDSTATEMENTS, "200");
        prop.setProperty(DruidDataSourceFactory.PROP_REMOVEABANDONED, "true");
        // 监控统计拦截的filters
        prop.setProperty(DruidDataSourceFactory.PROP_FILTERS, "stat");

        DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(prop);
        map.put(url, druidDataSource);
        return druidDataSource;
    }
}
