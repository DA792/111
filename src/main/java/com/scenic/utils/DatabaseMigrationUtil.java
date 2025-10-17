package com.scenic.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * 数据库迁移工具类
 * 用于在应用启动时执行SQL脚本，进行数据库结构更新
 */
@Component
public class DatabaseMigrationUtil implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(DatabaseMigrationUtil.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 执行添加activity_time字段的SQL脚本
        executeSqlFile("src/main/resources/sql/add_activity_time_column.sql");
    }

    /**
     * 执行SQL文件
     * @param filePath SQL文件路径
     */
    private void executeSqlFile(String filePath) {
        try {
            String sql = new String(Files.readAllBytes(Paths.get(filePath)));
            
            // 分割SQL语句（按分号分割）
            String[] sqlStatements = sql.split(";");
            
            for (String statement : sqlStatements) {
                if (statement.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // 执行SQL语句
                    jdbcTemplate.execute(statement);
                    logger.info("成功执行SQL语句: " + statement);
                } catch (Exception e) {
                    // 如果字段已存在，会抛出异常，这里捕获并记录
                    logger.warning("执行SQL语句时出错: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("读取SQL文件时出错: " + e.getMessage());
        }
    }
}
