package com.yizlan.code.gen.factory;

import com.baomidou.mybatisplus.generator.config.IKeyWordsHandler;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.baomidou.mybatisplus.generator.keywords.PostgreSqlKeyWordsHandler;
import com.yizlan.code.gen.constant.Constants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * DB关键字工厂
 *
 * @author Alias Zen
 * @version 1.0
 * @since 2022-12-07
 */
@Component
public class DbKeyWordsFactory {

    public IKeyWordsHandler getKeyWordsHandler(String dbType) {
        if (StringUtils.hasText(dbType)) {
            if (Constants.DataBaseType.MYSQL.equals(dbType)) {
                return new MySqlKeyWordsHandler();
            } else if (Constants.DataBaseType.POSTGRE_SQL.equals(dbType)) {
                return new PostgreSqlKeyWordsHandler();
            }
        }
        return null;
    }
}
