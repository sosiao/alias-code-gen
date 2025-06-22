package com.yizlan.code.gen.controller;

import com.yizlan.code.gen.dto.GeneratorParam;
import com.yizlan.code.gen.dto.JdbcParam;
import com.yizlan.code.gen.dto.ProjectSetting;
import com.yizlan.code.gen.dto.TableInfo;
import com.yizlan.code.gen.service.GenerateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 代码生成
 *
 * @author Alias Zen
 * @version 1.0
 * @since 2022-11-24
 */
@RestController
public class GeneratorController {

    @Resource
    private GenerateService generateService;

    /**
     * 获取数据库类型
     *
     * @return 数据库类型
     */
    @GetMapping("/generator/init")
    public ProjectSetting init() {
        return generateService.init();
    }

    /**
     * 获取数据表
     *
     * @param jdbcParam 数据库参数
     * @return 数据表
     */
    @PostMapping("/generator/tables")
    public List<TableInfo> getTableList(@RequestBody JdbcParam jdbcParam) {
        return generateService.getTableList(jdbcParam);
    }

    /**
     * 生成代码
     *
     * @param generatorParam 生成参数
     */
    @PostMapping("/generator/generate")
    public void generateCode(@RequestBody GeneratorParam generatorParam) {
        generateService.generateCode(generatorParam);
    }
}
