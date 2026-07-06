# 后端服务运行说明

## 项目结构

```
workspace-java/easylive-java/
├── easylive-common/       # 公共模块
├── easylive-admin/        # 后台管理系统后端
├── easylive-web/          # 前端接口后端
└── pom.xml                # 父项目配置
```

## 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis

## 构建与运行

### 1. 构建项目

```bash
# 清理并构建整个项目
mvn clean install -DskipTests

# 单独构建某个模块（可选）
mvn clean install -DskipTests -pl easylive-common
mvn clean install -DskipTests -pl easylive-admin
mvn clean install -DskipTests -pl easylive-web
```

### 2. 运行后台管理系统

```bash
# 启动 easylive-admin（端口：7069）
mvn spring-boot:run -pl easylive-admin

# 访问地址
http://localhost:7069/admin
```

### 3. 运行前端接口服务

```bash
# 启动 easylive-web（端口：7071）
mvn spring-boot:run -pl easylive-web

# 访问地址
http://localhost:7071
```

## 配置说明

### 数据库配置

修改 `application.yml` 文件中的数据库连接信息：

- easylive-admin/src/main/resources/application.yml
- easylive-web/src/main/resources/application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3310/easylive?allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: 123456
```

### Redis配置

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6380
```

### 阿里云配置（视频审核）

```yaml
aliyun:
  access-key-id: your_access_key_id
  access-key-secret: your_access_key_secret
  region: cn-shanghai
```

## 常见问题

### 1. 依赖问题

如果遇到依赖下载失败，尝试：

```bash
# 清理 Maven 缓存
mvn dependency:purge-local-repository

# 重新构建
mvn clean install -DskipTests
```

### 2. 端口冲突

如果端口被占用，修改 `application.yml` 中的端口配置：

```yaml
server:
  port: 7069  # 修改为其他端口
```

### 3. 数据库连接失败

- 确保 MySQL 服务已启动
- 确保数据库 `easylive` 已创建
- 确保用户名和密码正确

### 4. Redis 连接失败

- 确保 Redis 服务已启动
- 确保端口配置正确

## 视频审核功能

系统集成了阿里云视频审核功能，自动对上传的视频进行内容审核：

1. 视频上传后自动转码
2. 转码完成后自动抽取视频帧（每5秒抽一帧）
3. 调用阿里云 API 对抽取的帧进行审核
4. 审核通过的视频自动发布，不通过的视频进入人工复核队列

## 开发环境建议

- 使用 IntelliJ IDEA 或 Eclipse 作为开发工具
- 安装 Lombok 插件以支持注解处理
- 配置 Maven 镜像加速依赖下载
