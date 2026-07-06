package com.easyjava.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * 在 Spring 环境初始化最早期加载项目根目录的 .env 文件，
 * 将其中的键值对注入到 Spring Environment 中，使其可被 ${...} 引用。
 * 优先级：系统环境变量 > .env 文件 > application.yml 默认值
 */
public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            Properties props = new Properties();
            dotenv.entries().forEach(entry -> {
                // 只注入系统环境中不存在的 key，避免覆盖真实环境变量
                if (System.getenv(entry.getKey()) == null) {
                    props.setProperty(entry.getKey(), entry.getValue());
                }
            });

            if (!props.isEmpty()) {
                environment.getPropertySources()
                        .addLast(new PropertiesPropertySource("dotenv", props));
            }
        } catch (Exception e) {
            // .env 文件不存在或解析失败时静默跳过，不影响启动
            System.out.println("[DotEnv] 未加载 .env 文件: " + e.getMessage());
        }
    }
}
