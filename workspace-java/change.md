# 项目修改记录

## 2026-03-26

### 1. 修复编译问题

- **StringTools.java** - 移除不存在的 `com.sun.prism.shader` 导入
- **VideoController.java** - 移除不存在的 `com.sun.xml.internal.bind.v2.TODO` 导入
- **easylive-admin/pom.xml** - 修复 mainClass 配置路径错误
- **easylive-common/pom.xml** - 添加 Nashorn 引擎依赖解决验证码问题

### 2. 配置文件修改

- **easylive-admin/application.yml** - MySQL 端口改为 3310，Redis 端口改为 6380
- **easylive-web/application.yml** - MySQL 端口改为 3310，Redis 端口改为 6380

### 3. 视频审核功能设计

- 新增数据库设计文档 [audit_design.md](../workspace-web/jollylife-front/jollylife-front-admin/audit_design.md)

### 4. 数据库表结构修改 (easylive.sql)

#### 4.1 tb_video_info_post 表新增字段

```sql
-- AI审核相关字段
`ai_audit_status`     TINYINT  -- AI审核状态: 0-待审核, 1-通过, 2-不通过
`ai_audit_result`     TEXT     -- AI审核返回的详细结果(JSON)
`ai_audit_time`       DATETIME -- AI审核时间
`ai_audit_reason`     VARCHAR(500) -- AI审核不通过原因

-- 人工复核相关字段
`review_status`       TINYINT  -- 人工复核状态: 0-待复核, 1-通过, 2-不通过
`review_time`         DATETIME -- 人工复核时间
`reviewer_id`         VARCHAR(50) -- 复核人ID
`review_reason`       VARCHAR(500) -- 人工复核不通过原因
```

#### 4.2 新增索引

```sql
INDEX `idx_status`(`status`)
INDEX `idx_review_status`(`review_status`)
```

#### 4.3 新建 tb_audit_config 表

```sql
CREATE TABLE tb_audit_config (
    `id`               INT PRIMARY KEY AUTO_INCREMENT,
    `config_key`       VARCHAR(100) NOT NULL,
    `config_value`     TEXT,
    `description`      VARCHAR(200),
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)
```

初始化配置项：
- ai_provider: openai
- ai_api_key: (需配置)
- ai_model: gpt-4o
- ai_auto_publish: true

### 5. 视频状态枚举说明 (VideoStatusEnum)

| 状态码 | 说明 |
|--------|------|
| 0 | 转码中 |
| 1 | 转码失败 |
| 2 | 待审核 |
| 3 | 审核成功/已发布 |
| 4 | 审核不通过（人工复核不通过）|
| 5 | 待人工复核（AI审核不通过，需人工确认）|

### 6. 后端代码修改

#### 6.1 实体类和枚举

- **VideoStatusEnum.java** - 新增 STATUS5(5, "待人工复核")
- **VideoInfoPost.java** - 新增 AI审核和人工复核相关字段及getter/setter
- **VideoInfoPostquery.java** - 新增 aiAuditStatus 和 reviewStatus 查询字段

#### 6.2 Mapper 和 XML

- **VideoInfoPostMapper.xml** - 更新 resultMap 和 base_column_list，添加新字段映射和查询条件
- **AuditConfigMapper.java** - 新建审核配置 Mapper 接口
- **AuditConfigMapper.xml** - 新建审核配置 Mapper XML

#### 6.3 新建实体类

- **AuditConfig.java** - 审核配置实体类
- **AiAuditResult.java** - AI审核结果 DTO

#### 6.4 服务层

- **AiAuditService.java** - AI审核服务接口，新增 `auditVideoFrames` 方法
- **OpenAiAuditServiceImpl.java** - 阿里云图片审核服务实现

#### 6.5 控制器

- **VideoAuditController.java** - 新建视频审核控制器
  - `POST /admin/video/audit` - 触发AI审核
  - `GET /admin/video/review/list` - 获取待复核列表
  - `POST /admin/video/review` - 人工复核操作

### 7. 阿里云视频审核集成

#### 7.1 依赖添加

- **easylive-common/pom.xml** - 添加阿里云内容安全SDK依赖

```xml
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>green202203032</artifactId>
    <version>2.0.2</version>
</dependency>
```

#### 7.2 FFmpeg 工具类扩展

- **FFmpegUtils.java** - 新增 `extractFramesEveryNSeconds` 方法
  - 每隔指定秒数抽取视频帧
  - 支持最大帧数限制
  - 自动清理临时帧文件

#### 7.3 审核服务实现

- **AiAuditService.java** - 新增接口方法

```java
AiAuditResult auditVideoFrames(String videoId, String videoFilePath);
```

- **OpenAiAuditServiceImpl.java** - 完整实现

  1. 使用 FFmpeg 每隔5秒抽取视频帧（最多10帧）
  2. 对每帧调用阿里云 `aigcDetectorForFrame` 接口进行审核
  3. 任一帧不通过则标记为不通过
  4. 审核完成后自动清理临时帧文件

#### 7.4 审核配置

在 `application.yml` 中配置阿里云信息：

```yaml
aliyun:
  access-key-id: your_access_key_id
  access-key-secret: your_access_key_secret
  region: cn-shanghai
```

配置文件：
- **easylive-admin/application.yml**
- **easylive-web/application.yml**
- **AppConfig.java** - 新增 aliyunAccessKeyId、aliyunAccessKeySecret、aliyunRegion 属性

#### 7.5 审核服务说明

| 服务 | 说明 |
|------|------|
| aigcDetectorForFrame | AI生成图片鉴别_视频截图版 |
| 审核内容 | 视频截帧是否疑似AI生成合成 |
| 计费 | 30元/万次（高级版）|

#### 7.6 自动审核集成

- **VideoInfoPostServiceImpl.java** - 转码完成后自动触发AI审核
  - 在 `transferVideoFile()` 方法中，当所有文件转码成功后自动调用 AI 审核
  - 审核通过：状态变为 `STATUS3`（审核成功/已发布）
  - 审核不通过：状态变为 `STATUS5`（待人工复核）

```java
// 转码完成后自动审核
if (transferCount == 0) {
    // ... 更新状态为待审核
    auditVideoAfterTranscode(videoId);  // 自动触发AI审核
}
```

审核流程：
1. 获取转码成功的视频文件路径
2. 调用 `aiAuditService.auditVideoFrames()` 进行帧审核
3. 根据审核结果更新视频状态和AI审核结果