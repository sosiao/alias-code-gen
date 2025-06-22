package com.yizlan.code.gen.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.baomidou.mybatisplus.generator.config.IKeyWordsHandler;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.Controller;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.builder.Mapper;
import com.baomidou.mybatisplus.generator.config.querys.DbQueryDecorator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.yizlan.code.gen.common.dictionary.SystemKeyValue;
import com.yizlan.code.gen.common.support.ServiceAssert;
import com.yizlan.code.gen.constant.Constants;
import com.yizlan.code.gen.dto.GeneratorParam;
import com.yizlan.code.gen.dto.JdbcParam;
import com.yizlan.code.gen.dto.ProjectSetting;
import com.yizlan.code.gen.dto.TableInfo;
import com.yizlan.code.gen.enums.DbTypeEnum;
import com.yizlan.code.gen.enums.MapperXmlPathEnum;
import com.yizlan.code.gen.factory.DbKeyWordsFactory;
import com.yizlan.code.gen.service.GenerateService;
import com.yizlan.gelato.canonical.enums.BiEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 生成代码 服务实现类
 *
 * @author Alias Zen
 * @version 1.0
 * @since 2022-11-24
 */
@Slf4j
@Service
public class GenerateServiceImpl extends AbstractGenerateServiceImpl implements GenerateService {

    @Resource
    private DbKeyWordsFactory dbKeyWordsFactory;

    @Override
    public ProjectSetting init() {
        List<SystemKeyValue> dbTypes = BiEnum.toList(DbTypeEnum.values(), SystemKeyValue::new);

        List<SystemKeyValue> xmlDirs = BiEnum.toList(MapperXmlPathEnum.values(), SystemKeyValue::new);

        return ProjectSetting.builder().dbTypes(dbTypes).xmlDirs(xmlDirs).build();
    }

    @Override
    public List<TableInfo> getTableList(JdbcParam jdbcParam) {
        DataSourceConfig.Builder builder = this.createDataSourceConfig(jdbcParam);

        StrategyConfig.Builder strategy = new StrategyConfig.Builder();
        DbQueryDecorator dbQuery = new DbQueryDecorator(builder.build(), strategy.build());
        String tablesSql = dbQuery.tablesSql();
        String name = dbQuery.tableName();

        List<TableInfo> tableInfos = new ArrayList<>();
        try {
            dbQuery.execute(tablesSql, rs -> {
                String tableName = rs.getStringResult(name);
                String tableComment = rs.getTableComment();
                tableInfos.add(TableInfo.builder().id(IdWorker.getIdStr()).tableName(tableName).tableComment(tableComment).build());
            });
        } catch (SQLException e) {
            ServiceAssert.throwException(String.valueOf(e.getErrorCode()), e.getMessage());
        } finally {
            dbQuery.closeConnection();
        }

        return tableInfos;
    }

    @Override
    public void generateCode(GeneratorParam generatorParam) {
        FastAutoGenerator.create(dataSourceConfigBuilder(generatorParam))
                .globalConfig(builder -> configGlobal(builder, generatorParam))
                .packageConfig(builder -> configPackage(builder, generatorParam))
                .strategyConfig(builder -> configDefaultStrategy(builder, generatorParam))
                .strategyConfig(builder -> configEntityStrategy(builder, generatorParam))
                .strategyConfig(builder -> configCtrlStrategy(builder, generatorParam))
                .strategyConfig(builder -> configServiceStrategy(builder, generatorParam))
                .strategyConfig(builder -> configMapperStrategy(builder, generatorParam))
                .injectionConfig(builder -> builder.beforeOutputFile((tableInfo, objectMap) -> {
                            generatorParam.getGenerateTableList().forEach(m -> {
                                if (tableInfo.getName().equals(m.getTableName())) {
                                    tableInfo.setComment(m.getTableComment());
                                }
                            });
                            if (!Boolean.TRUE.equals(generatorParam.getUseTableAnnotation())) {
                                tableInfo.setConvert(false);
                            }
                        })
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    private DataSourceConfig.Builder dataSourceConfigBuilder(GeneratorParam generatorParam) {
        DataSourceConfig.Builder builder = this.createDataSourceConfig(JdbcParam.builder()
                .dbType(generatorParam.getDbType())
                .host(generatorParam.getHost())
                .dbName(generatorParam.getDbName())
                .userName(generatorParam.getUserName())
                .password(generatorParam.getPassword())
                .build());

        IDbQuery dbQuery = builder.build().getDbQuery();
        builder.dbQuery(dbQuery);

        if (Constants.DataBaseType.containDbKeywords(generatorParam.getDbType())) {
            IKeyWordsHandler keyWordsHandler = dbKeyWordsFactory.getKeyWordsHandler(generatorParam.getDbType());
            Optional.ofNullable(keyWordsHandler).ifPresent(builder::keyWordsHandler);
        }

        if (StringUtils.hasText(generatorParam.getSchema())) {
            builder.schema(generatorParam.getSchema());
        }
        return builder;
    }

    private void configGlobal(GlobalConfig.Builder builder, GeneratorParam generatorParam) {
        String basePath = Constants.CodeTargetDir.JAVA;
        // 标识kotlin与lombok不可共存
        if (Boolean.TRUE.equals(generatorParam.getKotlinProject()) && !Boolean.TRUE.equals(generatorParam.getUseLombok())) {
            builder.enableKotlin();
            basePath = Constants.CodeTargetDir.KOTLIN;
        }
        if (Boolean.TRUE.equals(generatorParam.getUseSwagger())) {
            builder.enableSwagger();
        }
        builder.outputDir(generatorParam.getProjectLocation().concat(basePath))
                .author(generatorParam.getDeveloper())
                .dateType(DateType.ONLY_DATE);
    }

    private void configPackage(PackageConfig.Builder builder, GeneratorParam generatorParam) {
        builder.parent(generatorParam.getPackageName());
        // 包名
        if (StringUtils.hasText(generatorParam.getPackageEntity())) {
            builder.entity(generatorParam.getPackageEntity());
        }
        if (StringUtils.hasText(generatorParam.getPackageCtrl())) {
            builder.controller(generatorParam.getPackageCtrl());
        }
        if (StringUtils.hasText(generatorParam.getPackageService())) {
            builder.service(generatorParam.getPackageService());
        }
        if (StringUtils.hasText(generatorParam.getPackageServiceImpl())) {
            builder.serviceImpl(generatorParam.getPackageServiceImpl());
        }
        if (StringUtils.hasText(generatorParam.getPackageMapper())) {
            builder.mapper(generatorParam.getPackageMapper());
        }
        if (StringUtils.hasText(generatorParam.getPackageMapperXml())) {
            builder.xml(generatorParam.getPackageMapperXml());
        }

        if (Constants.MapperXmlDir.RESOURCES.equals(generatorParam.getXmlTargetDir())) {
            // 包转为resources下xml位置
            String packageDir = builder.build().getPackageInfo(ConstVal.XML).replace(StringPool.DOT, StringPool.SLASH);

            String fullPath = generatorParam.getProjectLocation() +
                    Constants.CodeTargetDir.RESOURCES + StringPool.SLASH + packageDir;
            builder.pathInfo(Collections.singletonMap(OutputFile.xml, fullPath));
        }
    }

    private void configDefaultStrategy(StrategyConfig.Builder builder, GeneratorParam generatorParam) {
        builder.addInclude(generatorParam.getGenerateTableList().stream().map(TableInfo::getTableName).collect(Collectors.toList()));
        // 数据表
        if (StringUtils.hasText(generatorParam.getTablePrefix())) {
            builder.addTablePrefix(generatorParam.getTablePrefix());
        }
        if (StringUtils.hasText(generatorParam.getTableSuffix())) {
            builder.addTableSuffix(generatorParam.getTableSuffix());
        }
        if (StringUtils.hasText(generatorParam.getTableFieldPrefix())) {
            builder.addFieldPrefix(generatorParam.getTableFieldPrefix());
        }
        if (StringUtils.hasText(generatorParam.getTableFieldSuffix())) {
            builder.addFieldSuffix(generatorParam.getTableFieldSuffix());
        }
    }

    private void configEntityStrategy(StrategyConfig.Builder builder, GeneratorParam generatorParam) {
        Entity.Builder entityBuilder = builder.entityBuilder().fileOverride()
                .naming(NamingStrategy.underline_to_camel);
        // 父类
        if (StringUtils.hasText(generatorParam.getSuperClassEntity())) {
            entityBuilder.superClass(generatorParam.getSuperClassEntity());
        }
        if (Boolean.TRUE.equals(generatorParam.getDisableSerialization())) {
            entityBuilder.disableSerialVersionUID();
        }
        // 标识kotlin与lombok不可共存
        boolean flag = generatorParam.getKotlinProject() == null || !generatorParam.getKotlinProject();
        if (Boolean.TRUE.equals(generatorParam.getUseLombok()) && flag) {
            entityBuilder.enableLombok();
        }
        if (Boolean.TRUE.equals(generatorParam.getUseTableFieldAnnotation())) {
            entityBuilder.enableTableFieldAnnotation();
        }
        if (StringUtils.hasText(generatorParam.getFileNameEntity())) {
            entityBuilder.formatFileName(generatorParam.getFileNameEntity());
        }
    }

    private void configCtrlStrategy(StrategyConfig.Builder builder, GeneratorParam generatorParam) {
        Controller.Builder ctrlBuilder = builder.controllerBuilder().fileOverride();
        // 父类
        if (StringUtils.hasText(generatorParam.getSuperClassCtrl())) {
            ctrlBuilder.superClass(generatorParam.getSuperClassCtrl());
        }
        if (Boolean.TRUE.equals(generatorParam.getUseRestStyle())) {
            ctrlBuilder.enableRestStyle();
        }
        if (Boolean.TRUE.equals(generatorParam.getUseHyphen())) {
            ctrlBuilder.enableHyphenStyle();
        }
        if (StringUtils.hasText(generatorParam.getFileNameCtrl())) {
            ctrlBuilder.formatFileName(generatorParam.getFileNameCtrl());
        }
    }

    private void configServiceStrategy(StrategyConfig.Builder builder, GeneratorParam generatorParam) {
        com.baomidou.mybatisplus.generator.config.builder.Service.Builder serviceBuilder =
                builder.serviceBuilder().fileOverride();
        // 父类
        if (StringUtils.hasText(generatorParam.getSuperClassService())) {
            serviceBuilder.superServiceClass(generatorParam.getSuperClassService());
        }
        if (StringUtils.hasText(generatorParam.getSuperClassServiceImpl())) {
            serviceBuilder.superServiceImplClass(generatorParam.getSuperClassServiceImpl());
        }
        // 文件名
        if (StringUtils.hasText(generatorParam.getFileNameService())) {
            serviceBuilder.formatServiceFileName(generatorParam.getFileNameService());
        }
        if (StringUtils.hasText(generatorParam.getFileNameServiceImpl())) {
            serviceBuilder.formatServiceImplFileName(generatorParam.getFileNameServiceImpl());
        }
    }

    private void configMapperStrategy(StrategyConfig.Builder builder, GeneratorParam generatorParam) {
        Mapper.Builder mapperBuilder = builder.mapperBuilder().fileOverride();
        // 父类
        if (StringUtils.hasText(generatorParam.getSuperClassMapper())) {
            mapperBuilder.superClass(generatorParam.getSuperClassMapper());
        }
        if (Boolean.TRUE.equals(generatorParam.getUseMapperAnnotation())) {
            mapperBuilder.enableMapperAnnotation();
        }
        if (Boolean.TRUE.equals(generatorParam.getGenerateBaseResultMap())) {
            mapperBuilder.enableBaseResultMap();
        }
        if (Boolean.TRUE.equals(generatorParam.getGenerateBaseColumnList())) {
            mapperBuilder.enableBaseColumnList();
        }
        // 文件名
        if (StringUtils.hasText(generatorParam.getFileNameMapper())) {
            mapperBuilder.formatMapperFileName(generatorParam.getFileNameMapper());
        }
        if (StringUtils.hasText(generatorParam.getFileNameMapperXml())) {
            mapperBuilder.formatXmlFileName(generatorParam.getFileNameMapperXml());
        }
    }
}
