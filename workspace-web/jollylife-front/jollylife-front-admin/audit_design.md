# 视频审核功能设计方案

## 一、现有系统状态分析

### 1.1 当前视频状态枚举
```
STATUS0(0, "转码中")
STATUS1(1, "转码失败")
STATUS2(2, "待审核")
STATUS3(3, "审核成功")
STATUS4(4, "审核不通过")
```

### 1.2 现有数据表
- `tb_video_info_post` - 用户投稿视频信息表
- `tb_video_info` - 正式发布的视频信息表
- `tb_video_info_file_post` - 用户投稿的视频文件表
- `tb_video_info_file` - 正式发布的视频文件表

---

## 二、需求分析

### 2.1 业务流程
```
用户上传视频
    ↓
转码处理 (STATUS=0 转码中)
    ↓
转码完成 (STATUS=2 待审核)
    ↓
调用大模型API进行AI审核
    ↓
┌─────────────────────────────────────────────┐
│  AI审核结果：                                │
│  1. 审核通过 → 直接发布 (STATUS=3)          │
│  2. 审核不通过 → 待人工复核 (STATUS=5)      │
└─────────────────────────────────────────────┘
    ↓
工作人员可在后台查看待复核视频
    ↓
人工复核
    ↓
┌─────────────────────────────────────────────┐
│  人工复核结果：                              │
│  1. 复核通过 → 正式发布 (STATUS=3)          │
│  2. 复核不通过 → 下架处理 (STATUS=4)        │
└─────────────────────────────────────────────┘
```

### 2.2 新增状态说明
| 状态码 | 状态名称 | 说明 |
|--------|----------|------|
| 0 | 转码中 | 现有 |
| 1 | 转码失败 | 现有 |
| 2 | 待审核 | 现有（AI审核前） |
| 3 | 审核成功/已发布 | 现有 |
| 4 | 审核不通过 | 现有（人工复核不通过） |
| 5 | 待人工复核 | **新增** - AI审核不通过，需要人工确认 |
| 6 | AI审核通过 | **新增** - AI审核通过，可直接发布 |

---

## 三、数据库设计

### 3.1 扩展 `tb_video_info_post` 表

```sql
-- 在 tb_video_info_post 表中新增字段
ALTER TABLE tb_video_info_post
ADD COLUMN ai_audit_status TINYINT DEFAULT NULL COMMENT 'AI审核状态: 0-待审核, 1-通过, 2-不通过',
ADD COLUMN ai_audit_result TEXT COMMENT 'AI审核返回的详细结果(JSON)',
ADD COLUMN ai_audit_time DATETIME DEFAULT NULL COMMENT 'AI审核时间',
ADD COLUMN ai_audit_reason VARCHAR(500) DEFAULT NULL COMMENT 'AI审核不通过原因',
ADD COLUMN review_status TINYINT DEFAULT NULL COMMENT '人工复核状态: 0-待复核, 1-通过, 2-不通过',
ADD COLUMN review_time DATETIME DEFAULT NULL COMMENT '人工复核时间',
ADD COLUMN reviewer_id VARCHAR(50) DEFAULT NULL COMMENT '复核人ID',
ADD COLUMN review_reason VARCHAR(500) DEFAULT NULL COMMENT '人工复核不通过原因';
```

### 3.2 新建 AI审核配置表（可选）

```sql
CREATE TABLE tb_audit_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(200) COMMENT '说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '审核配置表';

-- 初始数据
INSERT INTO tb_audit_config (config_key, config_value, description) VALUES
('ai_provider', 'openai', 'AI服务提供商: openai/baidu/ali/tongyi'),
('ai_api_key', '', 'API Key'),
('ai_model', 'gpt-4o', '使用的模型'),
('ai_auto_publish', 'true', 'AI审核通过后是否自动发布');
```

---

## 四、后端接口设计

### 4.1 新增接口

#### 4.1.1 视频AI审核（触发审核）
```
POST /admin/video/audit
参数: videoId (视频ID)
返回: 审核结果
```

#### 4.1.2 获取待人工复核列表
```
GET /admin/video/review/list
参数: page, pageSize, status(5-待复核)
返回: 视频列表 + AI审核结果
```

#### 4.1.3 人工复核操作
```
POST /admin/video/review
参数: videoId, action(pass/reject), reason(拒绝原因)
返回: 操作结果
```

#### 4.1.4 AI审核配置
```
GET/POST /admin/audit/config
```

### 4.2 需要修改的逻辑

1. **VideoInfoPostService** - 用户投稿服务
   - 新增 `auditVideo(videoId)` 方法调用AI审核
   - 新增 `manualReview(videoId, action, reason)` 方法处理人工复核

2. **文件上传完成后** - 自动触发AI审核
   - 转码成功后，自动调用AI审核接口

---

## 五、前端页面设计

### 5.1 视频列表页面改造

在现有视频列表中增加筛选和展示：
- 筛选条件：增加「待人工复核」「AI审核不通过」选项
- 列表展示：增加「AI审核状态」「人工复核状态」列
- 操作按钮：对于待复核视频，显示「人工复核」按钮

### 5.2 新增「审核管理」菜单

```
审核管理
├── AI审核记录
│   └── 查看所有AI审核结果
├── 待人工复核
│   └── 需要人工处理的视频列表
└── 审核配置
    └── 配置AI服务API Key等
```

### 5.3 待人工复核页面设计

```
┌─────────────────────────────────────────────────────────────┐
│ 待人工复核视频列表                                            │
├─────────────────────────────────────────────────────────────┤
│ 搜索: [视频名称] [用户ID] [时间范围]                         │
├─────────────────────────────────────────────────────────────┤
│ 视频信息   │ AI审核结果  │ AI不通过原因 │ 操作               │
├─────────────────────────────────────────────────────────────┤
│ 视频A     │ 不通过      │ 涉及敏感内容 │ [查看] [通过] [拒绝]│
│ 视频B     │ 不通过      │ 涉嫌违规     │ [查看] [通过] [拒绝]│
└─────────────────────────────────────────────────────────────┘
```

### 5.4 视频详情/复核弹窗

```
┌─────────────────────────────────────────────┐
│ 视频复核                                    │
├─────────────────────────────────────────────┤
│ 视频信息:                                   │
│   标题: xxx                                 │
│   分类: xxx                                 │
│   上传者: xxx                               │
│   上传时间: xxx                             │
├─────────────────────────────────────────────┤
│ AI审核结果:                                 │
│   审核状态: 不通过                          │
│   审核时间: 2026-03-26 10:00                │
│   不通过原因: 内容涉及xxx                   │
│   详细分析: (AI返回的完整JSON)              │
├─────────────────────────────────────────────┤
│ 人工复核:                                    │
│   [通过] [拒绝]                             │
│   拒绝原因: [输入框]                        │
└─────────────────────────────────────────────┘
```

---

## 六、大模型API接入设计

### 6.1 支持的AI服务
- OpenAI (GPT-4)
- 百度文心一言
- 阿里通义千问
- 腾讯混元

### 6.2 审核prompt示例

```
你是一个视频内容审核专家。请分析以下视频信息，判断是否包含违规内容：

视频标题：{videoTitle}
视频简介：{videoDescription}
视频标签：{tags}

请返回JSON格式的审核结果：
{
  "passed": true/false,
  "risk_level": "low/medium/high",
  "reason": "不通过的具体原因，如果有的话",
  "categories": ["违规类别1", "违规类别2"]
}

只返回JSON，不要其他内容。
```

### 6.3 接入方式

在 `easylive-common` 中新增 AI 审核服务类：

```
com.easyjava.service.AiAuditService
├── OpenAiAuditService
├── BaiduAuditService
├── AliAuditService
└── TencentAuditService
```

---

## 七、实施步骤

### 第一阶段：后端改造（优先级：高）
1. [ ] 修改数据库表结构，添加AI审核相关字段
2. [ ] 新增/修改 VideoStatusEnum 枚举
3. [ ] 创建 AI 审核服务接口和实现
4. [ ] 修改视频投稿相关Service
5. [ ] 新增审核管理相关接口

### 第二阶段：前端改造（优先级：高）
1. [ ] 修改视频列表，增加筛选和状态展示
2. [ ] 新增「待人工复核」页面
3. [ ] 新增「审核配置」页面
4. [ ] 新增复核操作弹窗

### 第三阶段：流程优化（优先级：中）
1. [ ] 转码完成后自动触发AI审核
2. [ ] 配置化管理AI服务参数
3. [ ] 审核日志记录

---

## 八、注意事项

1. **AI审核是异步的** - 需要考虑超时、重试机制
2. **敏感数据保护** - AI API Key 需要加密存储
3. **日志记录** - 所有审核操作需要记录日志便于追溯
4. **容错处理** - AI服务不可用时需要有降级策略（如直接进入人工审核）