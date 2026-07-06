# 前端页面与组件文件对照表

## 一、页面文件 (views)

### 1. 首页/布局
| 页面名称 | 文件路径 | 功能说明 |
|---------|---------|---------|
| 首页 | src/views/index/Index.vue | 网站首页，展示推荐视频 |
| 布局框架 | src/views/layout/Layout.vue | 整体页面布局容器 |
| 顶部导航 | src/views/layout/LayoutHeader.vue | 网站顶部导航栏 |
| 分类页面 | src/views/layout/Category.vue | 视频分类浏览 |

### 2. 视频相关
| 页面名称 | 文件路径 | 功能说明 |
|---------|---------|---------|
| 视频详情 | src/views/videoDetail/VideoDetail.vue | 视频播放页（含弹幕、评论） |
| 视频播放组件 | src/components/Player.vue | 视频播放器 |
| 弹幕播放器 | src/components/DanmuPlayer.vue | 弹幕显示与发送 |
| 视频列表 | src/views/videoList/videoList.vue | 视频列表展示 |
| 分类视频 | src/views/videoList/CategoryVideo.vue | 按分类展示视频 |
| 视频合集 | src/views/videoDetail/VideoPList.vue | 视频合集/系列 |

### 3. 用户中心
| 页面名称 | 文件路径 | 功能说明 |
|---------|---------|---------|
| 用户中心布局 | src/views/ucenter/UcLayout.vue | 用户中心整体布局 |
| 用户主页 | src/views/ucenter/Home.vue | 用户个人主页 |
| 消息中心 | src/views/ucenter/Message.vue | 用户消息列表 |
| 收藏列表 | src/views/ucenter/Collection.vue | 用户收藏的视频 |
| 观看历史 | src/views/ucenter/History.vue | 用户的观看历史 |
| 投稿页面 | src/views/ucenter/postVideo/Post.vue | 视频投稿入口 |
| 视频上传 | src/views/ucenter/postVideo/VideoUploadStart.vue | 开始上传视频 |
| 视频上传器 | src/views/ucenter/postVideo/VideoUploader.vue | 视频上传组件 |
| 标签输入 | src/views/ucenter/postVideo/TagInput.vue | 视频标签输入组件 |

### 4. 其他页面
| 页面名称 | 文件路径 | 功能说明 |
|---------|---------|---------|
| 搜索结果 | src/views/search/Search.vue | 搜索结果页面 |
| 用户主页 | src/views/userHome/UserHome.vue | 访问他人主页 |
| 账号设置 | src/views/account/Account.vue | 账号设置页面 |

---

## 二、可复用组件 (components)

| 组件名称 | 文件路径 | 功能说明 |
|---------|---------|---------|
| 视频卡片 | src/components/VideoItem.vue | 视频列表项组件 |
| 评论区 | src/components/CommentList.vue | 评论列表容器 |
| 评论项 | src/components/CommentItem.vue | 单条评论显示 |
| 评论输入 | src/components/CommentInput.vue | 评论输入框 |
| 互动栏 | src/components/ActionBar.vue | 点赞、投币、收藏等按钮 |
| 图片选择 | src/components/ImageCoverSelect.vue | 图片封面选择 |
| 图片裁剪 | src/components/ImageCoverCut.vue | 图片裁剪组件 |
| 对话框 | src/components/Dialog.vue | 通用弹窗 |
| 封面组件 | src/components/Cover.vue | 视频封面展示 |
| 头像组件 | src/components/Avatar.vue | 用户头像显示 |

---

## 三、修改美观度说明

### 独立修改页面的步骤：
1. 定位到对应的 `.vue` 文件
2. 文件中的 `<style scoped>` 部分是样式代码，修改这里不会影响其他页面
3. 也可以在文件根目录创建单独的 `.less` 或 `.css` 文件引入

### 推荐优先修改的页面（美观度提升明显）：
1. **首页** - `src/views/index/Index.vue`
2. **视频详情页** - `src/views/videoDetail/VideoDetail.vue`
3. **顶部导航** - `src/views/layout/LayoutHeader.vue`
4. **视频卡片** - `src/components/VideoItem.vue`
5. **用户中心** - `src/views/ucenter/Home.vue`

---

## 四、样式文件位置

- 全局样式：`src/assets/css/`
- Element Plus 主题变量：查找 `src/assets/` 目录下的主题配置文件
