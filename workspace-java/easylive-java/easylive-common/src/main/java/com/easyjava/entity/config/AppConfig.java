package com.easyjava.entity.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
/*这是一个应用配置类 AppConfig，它通过Spring的配置注解从配置文件中读取应用相关的配置参数*/
@Configuration
public class AppConfig {
    @Value("${project.folder:}")
    private String projectFolder;

    @Value("${admin.account:}")
    private String adminAccount;


    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${showFFmpegLog:true}")
    private boolean showFFmpegLog;

    @Value("${aliyun.access-key-id:}")
    private String aliyunAccessKeyId;

    @Value("${aliyun.access-key-secret:}")
    private String aliyunAccessKeySecret;

    @Value("${aliyun.region:cn-shanghai}")
    private String aliyunRegion;

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }
    public boolean isShowFFmpegLog() {
        return showFFmpegLog;
    }

    public String getAliyunAccessKeyId() {
        return aliyunAccessKeyId;
    }

    public String getAliyunAccessKeySecret() {
        return aliyunAccessKeySecret;
    }

    public String getAliyunRegion() {
        return aliyunRegion;
    }
}
