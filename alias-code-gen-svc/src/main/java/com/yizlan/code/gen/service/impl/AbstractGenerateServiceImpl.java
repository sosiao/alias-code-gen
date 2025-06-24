package com.yizlan.code.gen.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.yizlan.code.gen.common.support.ServiceAssert;
import com.yizlan.code.gen.dto.JdbcParam;
import com.yizlan.code.gen.enums.JdbcRegularUrl;
import com.yizlan.code.gen.i18n.I18nConstants;
import com.yizlan.code.gen.service.DruidService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 抽象生成方法
 *
 * @author Alias Zen
 * @version 1.0
 * @since 2022-11-27
 */
public class AbstractGenerateServiceImpl {

    @Resource
    private DruidService druidService;

    protected DataSourceConfig.Builder createDataSourceConfig(JdbcParam jdbcParam) {
        try {
            return new DataSourceConfig.Builder(createDataSource(jdbcParam));
        } catch (Exception e) {
            ServiceAssert.throwException(I18nConstants.DataSource.DS0001);
        }
        return null;
    }

    protected DruidDataSource createDataSource(JdbcParam jdbcParam) {
        String jdbcUrl = createJdbcUrl(jdbcParam);

        try {
            String driverClassName = JdbcUtils.getDriverClassName(jdbcUrl);
            return druidService.initDataSource(driverClassName, jdbcUrl, jdbcParam.getUserName(),
                    jdbcParam.getPassword(), jdbcParam.getDbType());
        } catch (Exception e) {
            ServiceAssert.throwException(I18nConstants.DataSource.DS0001);
        }
        return null;
    }

    private String createJdbcUrl(JdbcParam jdbcParam) {
        String url = "";
        DbType dbType = DbType.getDbType(jdbcParam.getDbType());
        switch (dbType) {
            case MYSQL:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getHost(), jdbcParam.getDbName());
                break;
            case MARIADB:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getHost(), jdbcParam.getDbName());
                break;
            case POSTGRE_SQL:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getHost(), jdbcParam.getDbName());
                break;
            case ORACLE:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getHost(), jdbcParam.getDbName());
                break;
            case SQL_SERVER:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getHost(), jdbcParam.getDbName());
                break;
            case DB2:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getHost(), jdbcParam.getDbName());
                break;
            case H2:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getHost(), jdbcParam.getDbName());
                break;
            case SQLITE:
                url = JdbcRegularUrl.match(jdbcParam.getDbType(), jdbcParam.getDbName());
                break;
            default:
                break;
        }

        ServiceAssert.isTrue(StringUtils.hasText(url), I18nConstants.Jdbc.JU0001);
        return url;
    }
}
