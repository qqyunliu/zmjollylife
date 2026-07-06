# 前端功能完善计划

## 一、项目现状分析

### 现有功能
| 功能模块 | 状态 | 说明 |
|---------|------|------|
| 首页推荐视频 | ✅ 已实现 | 轮播图 + 视频列表 |
| 视频播放 | ⚠️ 部分实现 | 播放器框架，交互需完善 |
| 弹幕系统 | ⚠️ 部分实现 | 已有界面，未完善发送功能 |
| 评论系统 | ⚠️ 接口定义 | 后端接口已有，前端未完善 |
| 点赞/投币/收藏 | ⚠️ 接口定义 | 后端接口已有，前端未完善 |
| 关注/粉丝 | ⚠️ 部分实现 | 关注按钮，逻辑需完善 |
| 用户个人主页 | ⚠️ 页面存在 | 功能不完善 |
| 搜索功能 | ⚠️ 接口定义 | 前端入口未完善 |
| 播放历史 | ⚠️ 接口定义 | 前端未实现 |
| 消息通知 | ⚠️ 接口定义 | 前端未实现 |

---

## 二、开发计划（分阶段）

### 阶段一：视频播放核心功能 ⭐优先级：高

#### 1.1 完善视频播放器
- [ ] 视频播放控制（播放/暂停、进度条、音量调节、全屏）
- [ ] 视频封面和加载状态
- [ ] 画中画模式支持
- [ ] 播放清晰度切换（如有多种分辨率）
- [ ] 记忆播放位置（续播功能）

#### 1.2 弹幕系统
- [ ] 弹幕显示优化（位置、颜色、透明度）
- [ ] 发送弹幕弹窗/输入框
- [ ] 弹幕开关控制
- [ ] 弹幕滚动效果
- [ ] 弹幕屏蔽功能（关键词过滤）

#### 1.3 视频信息区域
- [ ] 视频标题、简介、标签
- [ ] 播放数、弹幕数、发布时间
- [ ] 分类信息

---

### 阶段二：互动功能 ⭐优先级：高

#### 2.1 点赞功能
- [ ] 点赞按钮（视频点赞）
- [ ] 点赞动画效果
- [ ] 点赞数实时更新
- [ ] 点赞状态持久化

#### 2.2 投币功能
- [ ] 投币按钮
- [ ] 投币选择弹窗（1-2枚）
- [ ] 投币动画效果
- [ ] 投币数更新
- [ ] 用户硬币余额检查

#### 2.3 收藏功能
- [ ] 收藏按钮
- [ ] 收藏状态反馈
- [ ] 收藏列表页面
- [ ] 取消收藏

#### 2.4 一键三连（扩展）
- [ ] 三连按钮
- [ ] 同时完成点赞+投币+收藏

---

### 阶段三：评论系统 ⭐优先级：高

#### 3.1 评论列表
- [ ] 评论展示（文字评论）
- [ ] 评论分页加载
- [ ] 热门评论置顶
- [ ] 评论时间显示
- [ ] 评论用户信息

#### 3.2发表评论
- [ ] 评论输入框
- [ ] 发表接口
- [ ] 评论成功刷新

#### 3.3 评论互动
- [ ] 评论点赞
- [ ] 评论回复（楼中楼）
- [ ] 回复展开/收起
- [ ] 删除自己的评论

#### 3.4 评论管理
- [ ] 热门评论标记
- [ ] 评论排序（时间/热度）

---

### 阶段四：用户社交 ⭐优先级：中

#### 4.1 关注系统
- [ ] 关注/取消关注功能
- [ ] 关注数、粉丝数展示
- [ ] 关注列表页面
- [ ] 粉丝列表页面
- [ ] 相互关注标识

#### 4.2 个人主页
- [ ] 主页头部（头像、昵称、简介、关注/粉丝数）
- [ ] 视频Tab（投稿的视频）
- [ ] 收藏Tab（收藏的视频）
- [ ] 播放历史Tab
- [ ] 编辑资料功能

#### 4.3 用户互动
- [ ] 访问他人主页
- [ ] 私信功能（可选，复杂度高）

---

### 阶段五：内容发现 ⭐优先级：中

#### 5.1 搜索功能
- [ ] 搜索入口
- [ ] 搜索结果页
- [ ] 热门搜索词
- [ ] 搜索历史记录
- [ ] 搜索结果分类（视频/用户）

#### 5.2 推荐系统
- [ ] 视频推荐模块
- [ ] 热门视频榜单
- [ ] 分类热门视频

#### 5.3 分类浏览
- [ ] 分类导航优化
- [ ] 分类视频列表
- [ ] 子分类筛选

---

### 阶段六：用户中心增强 ⭐优先级：中

#### 6.1 播放历史
- [ ] 历史记录页面
- [ ] 播放历史列表
- [ ] 删除单条/清空历史

#### 6.2 消息通知
- [ ] 消息中心入口
- [ ] 未读消息数量徽章
- [ ] 系统通知列表
- [ ] 互动通知（点赞、评论、关注）
- [ ] 消息已读/删除

#### 6.3 账户设置
- [ ] 头像修改
- [ ] 昵称修改
- [ ] 密码修改
- [ ] 主题切换（深色/浅色）

---

### 阶段七：体验优化 ⭐优先级：低


#### 7.1 交互优化
- [ ] 加载状态骨架屏
- [ ] 空状态提示
- [ ] 错误处理提示


---

## 三、页面结构规划

```
├── 首页 (Index.vue)
│   ├── 轮播图
│   ├── 推荐视频
│   └── 热门视频
│
├── 视频播放页 (VideoDetail.vue)
│   ├── 播放器 (Player.vue)
│   ├── 弹幕层 (Danmu.vue)
│   ├── 视频信息
│   ├── 互动栏（点赞/投币/收藏/分享）
│   └── 评论区域
│       ├── 评论列表
│       └── 评论输入框
│
├── 分类页 (CategoryVideo.vue)
│   ├── 分类导航
│   └── 视频列表
│
├── 搜索页 (Search.vue) [新建]
│   ├── 搜索框
│   ├── 热门搜索
│   └── 搜索结果
│
├── 用户中心 (ucenter)
│   ├── 首页 (Home.vue)
│   ├── 投稿管理 (Post.vue)
│   ├── 播放历史 (History.vue) [新建]
│   ├── 收藏列表 (Collection.vue) [新建]
│   └── 消息中心 (Message.vue) [新建]
│
└── 个人主页 (UserHome.vue) [新建或完善]
    ├── 用户信息
    ├── 视频Tab
    ├── 收藏Tab
    └── 关注/粉丝
```

---

## 四、技术实现建议

### 1. 状态管理
- 使用 Vue 3 Composition API
- 可考虑使用 Pinia 进行状态管理（如需要跨组件共享状态）

### 2. 组件拆分建议
```
components/
├── VideoPlayer.vue      # 视频播放器
├── DanmuPlayer.vue      # 弹幕播放器
├── DanmuInput.vue       # 弹幕输入
├── CommentList.vue      # 评论列表
├── CommentItem.vue      # 单条评论
├── CommentInput.vue     # 评论输入
├── ActionBar.vue        # 点赞/投币/收藏栏
├── UserCard.vue         # 用户信息卡片
├── VideoCard.vue        # 视频卡片
└── EmptyState.vue       # 空状态组件
```

### 3. 接口对接
- 后端API已定义在前端 `Api.js` 中
- 部分接口需要确认后端实现状态
- 建议先完成接口自测

---

## 五、开发优先级建议

**第一批（立即开发）**：
1. 视频播放核心功能
2. 弹幕系统
3. 点赞/投币/收藏

**第二批（尽快开发）**：
4. 评论系统
5. 搜索功能

**第三批（完善阶段）**：
6. 个人主页完善
7. 播放历史
8. 消息通知

---

## 六、后端接口完成情况

### 6.1 已完成的数据库表

| 表名 | 说明 | 状态 |
|-----|------|------|
| play_history | 播放历史表 | ✅ 已创建 |
| user_message | 用户消息表 | ✅ 已创建 |
| user_focus | 用户关注表 | ✅ 已创建 |
| video_series | 视频系列表 | ✅ 已创建 |
| video_series_video | 系列视频关联表 | ✅ 已创建 |
| search_keyword | 搜索关键词表 | ✅ 已创建 |

### 6.2 已完成后端接口

#### 弹幕系统
- `POST /danmu/loadDanmu` - 获取弹幕列表
- `POST /danmu/postDanmu` - 发送弹幕
- `POST /danmu/delDanmu` - 删除弹幕

#### 评论系统
- `POST /comment/loadComment` - 获取评论列表
- `POST /comment/postComment` - 发表评论
- `POST /comment/userDelComment` - 删除评论
- `POST /comment/likeComment` - 点赞评论
- `POST /comment/hateComment` - 踩评论
- `POST /comment/topComment` - 置顶评论
- `POST /comment/cancelTopComment` - 取消置顶

#### 播放历史
- `POST /history/loadHistory` - 获取播放历史
- `POST /history/delHistory` - 删除单条历史
- `POST /history/cleanHistory` - 清空历史

#### 消息通知
- `POST /message/loadMessage` - 获取消息列表
- `POST /message/getNoReadCount` - 获取未读数量
- `POST /message/getNoReadCountGroup` - 分类未读数量
- `POST /message/readAll` - 全部已读
- `POST /message/delMessage` - 删除消息

#### 关注系统
- `POST /uhome/focus` - 关注用户
- `POST /uhome/cancelFocus` - 取消关注
- `POST /uhome/loadFocusList` - 获取关注列表
- `POST /uhome/loadFansList` - 获取粉丝列表

#### 搜索功能
- `POST /video/search` - 搜索视频
- `POST /video/getSearchKeywordTop` - 热门搜索词
- `POST /video/loadHotVideoList` - 热门视频
- `POST /video/getVideoRecommend` - 推荐视频

#### 视频系列
- `POST /uhome/series/loadVideoSeries` - 获取系列列表
- `POST /uhome/series/saveVideoSeries` - 创建/更新系列
- `POST /uhome/series/changeVideoSeriesSort` - 修改系列排序
- `POST /uhome/series/getVideoSeriesDetail` - 获取系列详情
- `POST /uhome/series/delVideoSeries` - 删除系列
- `POST /uhome/series/saveSeriesVideo` - 添加视频到系列
- `POST /uhome/series/delSeriesVideo` - 从系列移除视频
- `POST /uhome/series/loadAllVideo` - 获取系列视频
- `POST /uhome/series/loadVideoSeriesWithVideo` - 获取系列及视频

#### 用户中心
- `POST /uhome/getUserInfo` - 获取用户信息
- `POST /uhome/updateUserInfo` - 更新用户信息
- `POST /uhome/loadVideoList` - 用户视频列表
- `POST /uhome/loadUserCollection` - 收藏列表
- `POST /uhome/saveTheme` - 保存主题

#### 投稿管理
- `POST /ucenter/loadAllVideo` - 加载所有视频
- `POST /ucenter/loadComment` - 加载评论
- `POST /ucenter/delComment` - 删除评论
- `POST /ucenter/loadDanmu` - 加载弹幕
- `POST /ucenter/delDanmu` - 删除弹幕
- `POST /ucenter/getActualTimeStatisticsInfo` - 实时统计
- `POST /ucenter/getWeekStatisticsInfo` - 周统计
- `POST /ucenter/deleteVideo` - 删除视频
- `POST /ucenter/getVideoByVideoId` - 获取视频信息
- `POST /ucenter/saveVideoInteraction` - 保存互动信息

### 6.3 数据库SQL

新增表结构的SQL请参考 `easylive.sql` 文件，需要在数据库中执行以下表：
- play_history
- user_message
- user_focus
- video_series
- video_series_video
- search_keyword

---

## 七、注意事项

1. **数据库执行**：新增的表结构需要在MySQL中执行后才能正常使用
2. **Redis依赖**：部分接口需要Redis组件进行token验证
3. **文件上传**：系列封面等图片上传功能复用现有文件上传接口