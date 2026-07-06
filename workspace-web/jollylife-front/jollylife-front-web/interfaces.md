# JollyLife 接口文档

## 基础信息

- **服务地址**: `http://localhost:7072`
- **基础路径**: `/`
- **通用返回格式**:
  ```json
  {
    "code": 1,
    "msg": "success",
    "data": {}
  }
  ```
  - `code`: 1=成功, 0=失败
  - `msg`: 提示信息
  - `data`: 返回数据

---

## 一、账号相关 (account)

### 1.1 发送验证码
| 项目 | 内容 |
|-----|------|
| 接口 | `/account/checkCode` |
| 方法 | POST |
| 参数 | `email: String` (邮箱地址) |
| 返回 | `{code: 1, data: null}` |

### 1.2 注册
| 项目 | 内容 |
|-----|------|
| 接口 | `/account/register` |
| 方法 | POST |
| 参数 | `email: String, password: String, checkCode: String, nickname: String` |
| 返回 | `{code: 1, data: {token: "xxx"}}` |

### 1.3 登录
| 项目 | 内容 |
|-----|------|
| 接口 | `/account/login` |
| 方法 | POST |
| 参数 | `email: String, password: String` |
| 返回 | `{code: 1, data: {token: "xxx"}}` |

### 1.4 自动登录
| 项目 | 内容 |
|-----|------|
| 接口 | `/account/autoLogin` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: {token: "xxx"}}` |

### 1.5 登出
| 项目 | 内容 |
|-----|------|
| 接口 | `/account/logout` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: null}` |

---

## 二、视频相关 (video)

### 2.1 获取推荐视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/loadRecommendVideo` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [VideoInfo...]}` |

### 2.2 获取视频列表(分页)
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/loadVideo` |
| 方法 | POST |
| 参数 | `pCategoryId: Integer, categoryId: Integer, pageNo: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10, pageNo: 1, pageSize: 10}}` |

### 2.3 获取视频详情
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/getVideoInfo` |
| 方法 | POST |
| 参数 | `videoId: String` (必填) |
| 返回 | `{code: 1, data: VideoInfoResultVO}` |

### 2.4 获取视频分P列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/loadVideoPList` |
| 方法 | POST |
| 参数 | `videoId: String` (必填) |
| 返回 | `{code: 1, data: [VideoInfoFile...]}` |

### 2.5 搜索视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/search` |
| 方法 | POST |
| 参数 | `keyword: String` |
| 返回 | `{code: 1, data: [VideoInfo...]}` |

### 2.6 获取搜索热词
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/getSearchKeywordTop` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [String...]}` |

### 2.7 获取推荐视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/getVideoRecommend` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [VideoInfo...]}` |

### 2.8 获取热门视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/video/loadHotVideoList` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [VideoInfo...]}` |

---

## 三、分类相关 (category)

### 3.1 获取分类列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/category/loadCategory` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [{categoryId, categoryName, pCategoryId, ...}]}` |

---

## 四、评论相关 (comment)

### 4.1 获取评论列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/loadComment` |
| 方法 | POST |
| 参数 | `videoId: String, pageNo: Integer, pageSize: Integer, sortType: Integer` |
| 返回 | `{code: 1, data: {list: [VideoComment...], total: 10}}` |

### 4.2 获取回复列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/loadReply` |
| 方法 | POST |
| 参数 | `pCommentId: Integer` |
| 返回 | `{code: 1, data: [VideoComment...]}` |

### 4.3 发表评论
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/postComment` |
| 方法 | POST |
| 参数 | `videoId: String, content: String, pCommentId: Integer, replyUserId: String` |
| 返回 | `{code: 1, data: null}` |
| 备注 | 需要登录 |

### 4.4 删除评论
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/userDelComment` |
| 方法 | POST |
| 参数 | `commentId: Integer` |
| 返回 | `{code: 1, data: null}` |

### 4.5 点赞评论
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/likeComment` |
| 方法 | POST |
| 参数 | `commentId: Integer` |
| 返回 | `{code: 1, data: null}` |

### 4.6 踩评论
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/hateComment` |
| 方法 | POST |
| 参数 | `commentId: Integer` |
| 返回 | `{code: 1, data: null}` |

### 4.7 置顶评论
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/topComment` |
| 方法 | POST |
| 参数 | `commentId: Integer` |
| 返回 | `{code: 1, data: null}` |

### 4.8 取消置顶评论
| 项目 | 内容 |
|-----|------|
| 接口 | `/comment/cancelTopComment` |
| 方法 | POST |
| 参数 | `commentId: Integer` |
| 返回 | `{code: 1, data: null}` |

---

## 五、弹幕相关 (danmu)

### 5.1 加载弹幕
| 项目 | 内容 |
|-----|------|
| 接口 | `/danmu/loadDanmu` |
| 方法 | POST |
| 参数 | `videoId: String, fileId: String` |
| 返回 | `{code: 1, data: [VideoDanmu...]}` |

### 5.2 发送弹幕
| 项目 | 内容 |
|-----|------|
| 接口 | `/danmu/postDanmu` |
| 方法 | POST |
| 参数 | `videoId: String, fileId: String, content: String, color: String, time: Integer` |
| 返回 | `{code: 1, data: null}` |
| 备注 | 需要登录 |

### 5.3 删除弹幕
| 项目 | 内容 |
|-----|------|
| 接口 | `/danmu/delDanmu` |
| 方法 | POST |
| 参数 | `danmuId: Integer` |
| 返回 | `{code: 1, data: null}` |

---

## 六、用户行为 (userAction)

### 6.1 执行用户行为
| 项目 | 内容 |
|-----|------|
| 接口 | `/userAction/doAction` |
| 方法 | POST |
| 参数 | `videoId: String, actionType: Integer` |
| 返回 | `{code: 1, data: null}` |
| 说明 | actionType: 0=点赞, 1=踩, 2=投币, 3=收藏 |

### 6.2 获取用户行为
| 项目 | 内容 |
|-----|------|
| 接口 | `/userAction/getUserAction` |
| 方法 | POST |
| 参数 | `videoId: String, actionType: Integer` |
| 返回 | `{code: 1, data: UserAction}` |

### 6.3 获取视频行为统计
| 项目 | 内容 |
|-----|------|
| 接口 | `/userAction/getVideoActionCount` |
| 方法 | POST |
| 参数 | `videoId: String` |
| 返回 | `{code: 1, data: {likeCount, coinCount, collectCount, playCount}}` |

---

## 七、用户中心 (uhome)

### 7.1 获取用户信息
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/getUserInfo` |
| 方法 | POST |
| 参数 | `userId: String` |
| 返回 | `{code: 1, data: UserInfo}` |

### 7.2 更新用户信息
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/updateUserInfo` |
| 方法 | POST |
| 参数 | `nickname: String, sex: Integer, birthday: String, avatar: String` |
| 返回 | `{code: 1, data: null}` |

### 7.3 获取用户视频列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/loadVideoList` |
| 方法 | POST |
| 参数 | `userId: String, pageNo: Integer, pageSize: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10}}` |

### 7.4 获取用户收藏
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/loadUserCollection` |
| 方法 | POST |
| 参数 | `userId: String, pageNo: Integer, pageSize: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10}}` |

### 7.5 保存主题设置
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/saveTheme` |
| 方法 | POST |
| 参数 | `theme: String` |
| 返回 | `{code: 1, data: null}` |

### 7.6 关注用户
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/focus` |
| 方法 | POST |
| 参数 | `focusedUserId: String` |
| 返回 | `{code: 1, data: null}` |

### 7.7 取消关注
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/cancelFocus` |
| 方法 | POST |
| 参数 | `focusedUserId: String` |
| 返回 | `{code: 1, data: null}` |

### 7.8 获取关注列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/loadFocusList` |
| 方法 | POST |
| 参数 | `userId: String` |
| 返回 | `{code: 1, data: [UserInfo...]}` |

### 7.9 获取粉丝列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/loadFansList` |
| 方法 | POST |
| 参数 | `userId: String` |
| 返回 | `{code: 1, data: [UserInfo...]}` |

---

## 八、历史记录 (history)

### 8.1 获取观看历史
| 项目 | 内容 |
|-----|------|
| 接口 | `/history/loadHistory` |
| 方法 | POST |
| 参数 | `pageNo: Integer, pageSize: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10}}` |

### 8.2 删除单条历史
| 项目 | 内容 |
|-----|------|
| 接口 | `/history/delHistory` |
| 方法 | POST |
| 参数 | `videoId: String` |
| 返回 | `{code: 1, data: null}` |

### 8.3 清空历史记录
| 项目 | 内容 |
|-----|------|
| 接口 | `/history/cleanHistory` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: null}` |

---

## 九、消息相关 (message)

### 9.1 获取消息列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/message/loadMessage` |
| 方法 | POST |
| 参数 | `pageNo: Integer, pageSize: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10}}` |

### 9.2 获取未读消息数量
| 项目 | 内容 |
|-----|------|
| 接口 | `/message/getNoReadCount` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: 5}` |

### 9.3 获取未读消息数量(分组)
| 项目 | 内容 |
|-----|------|
| 接口 | `/message/getNoReadCountGroup` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: {like: 2, comment: 3, system: 0}}` |

### 9.4 标记全部已读
| 项目 | 内容 |
|-----|------|
| 接口 | `/message/readAll` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: null}` |

### 9.5 删除消息
| 项目 | 内容 |
|-----|------|
| 接口 | `/message/delMessage` |
| 方法 | POST |
| 参数 | `messageId: Integer` |
| 返回 | `{code: 1, data: null}` |

---

## 十、用户投稿 (ucenter)

### 10.1 投稿视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/postVideo` |
| 方法 | POST |
| 参数 | `videoId, title, categoryId, tags, description, videoType, fileId, cover` |
| 返回 | `{code: 1, data: null}` |

### 10.2 获取投稿视频列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/loadVideoList` |
| 方法 | POST |
| 参数 | `pageNo: Integer, pageSize: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10}}` |

### 10.3 获取视频统计信息
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/getVideoCountInfo` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: {total, viewing, notPass, checking}}` |

### 10.4 获取所有视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/loadAllVideo` |
| 方法 | POST |
| 参数 | `pageNo: Integer, pageSize: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10}}` |

### 10.5 获取视频评论管理
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/loadComment` |
| 方法 | POST |
| 参数 | `videoId: String, pageNo: Integer, pageSize: Integer` |
| 返回 | `{code: 1, data: {list: [...], total: 10}}` |

### 10.6 删除视频评论
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/delComment` |
| 方法 | POST |
| 参数 | `commentId: Integer` |
| 返回 | `{code: 1, data: null}` |

### 10.7 获取视频弹幕管理
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/loadDanmu` |
| 方法 | POST |
| 参数 | `videoId: String` |
| 返回 | `{code: 1, data: [VideoDanmu...]}` |

### 10.8 删除视频弹幕
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/delDanmu` |
| 方法 | POST |
| 参数 | `danmuId: Integer` |
| 返回 | `{code: 1, data: null}` |

### 10.9 获取实时统计
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/getActualTimeStatisticsInfo` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: {playCount, likeCount, coinCount, collectCount}}` |

### 10.10 获取周统计
| 项目 | 内容 |
|-----|------|
| 接口 | `/ucenter/getWeekStatisticsInfo` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [{date, playCount, likeCount, ...}]}` |

---

## 十一、视频系列 (uhome/series)

### 11.1 获取视频系列列表
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/loadVideoSeries` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [VideoSeries...]}` |

### 11.2 保存视频系列
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/saveVideoSeries` |
| 方法 | POST |
| 参数 | `seriesName: String, seriesCover: String` |
| 返回 | `{code: 1, data: null}` |

### 11.3 修改系列内视频排序
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/changeVideoSeriesSort` |
| 方法 | POST |
| 参数 | `seriesId: Integer, videoId: String, sort: Integer` |
| 返回 | `{code: 1, data: null}` |

### 11.4 获取系列详情
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/getVideoSeriesDetail` |
| 方法 | POST |
| 参数 | `seriesId: Integer` |
| 返回 | `{code: 1, data: VideoSeries}` |

### 11.5 删除视频系列
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/delVideoSeries` |
| 方法 | POST |
| 参数 | `seriesId: Integer` |
| 返回 | `{code: 1, data: null}` |

### 11.6 添加视频到系列
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/saveSeriesVideo` |
| 方法 | POST |
| 参数 | `seriesId: Integer, videoId: String` |
| 返回 | `{code: 1, data: null}` |

### 11.7 从系列移除视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/delSeriesVideo` |
| 方法 | POST |
| 参数 | `seriesId: Integer, videoId: String` |
| 返回 | `{code: 1, data: null}` |

### 11.8 获取系列内所有视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/loadAllVideo` |
| 方法 | POST |
| 参数 | `seriesId: Integer` |
| 返回 | `{code: 1, data: [VideoInfo...]}` |

### 11.9 获取视频系列(含视频)
| 项目 | 内容 |
|-----|------|
| 接口 | `/uhome/series/loadVideoSeriesWithVideo` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: [VideoSeries...]}` |

---

## 十二、文件相关 (file)

### 12.1 获取静态资源
| 项目 | 内容 |
|-----|------|
| 接口 | `/file/getResource` |
| 方法 | GET |
| 参数 | `sourceName: String` (如: cover/20241029/abc.jpg) |
| 返回 | 图片/文件流 |

### 12.2 预上传视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/file/preUploadVideo` |
| 方法 | POST |
| 参数 | `fileName: String, chunks: Integer` |
| 返回 | `{code: 1, data: "uploadId字符串"}` |
| 说明 | 生成uploadId用于后续分片上传 |

### 12.3 分片上传视频
| 项目 | 内容 |
|-----|------|
| 接口 | `/file/uploadVideo` |
| 方法 | POST |
| 参数 | `chunkFile: File, chunkIndex: Integer, uploadId: String` |
| 返回 | `{code: 1, data: null}` |
| 说明 | 需要登录，支持断点续传 |

### 12.4 删除上传
| 项目 | 内容 |
|-----|------|
| 接口 | `/file/delUploadVideo` |
| 方法 | POST |
| 参数 | `uploadId: String` |
| 返回 | `{code: 1, data: "uploadId"}` |

### 12.5 上传图片
| 项目 | 内容 |
|-----|------|
| 接口 | `/file/UploadImage` |
| 方法 | POST |
| 参数 | `file: File, createThumbnail: Boolean` |
| 返回 | `{code: 1, data: "cover/20241029/xxx.jpg"}` |

### 12.6 获取视频m3u8索引
| 项目 | 内容 |
|-----|------|
| 接口 | `/file/videoResource/{fileId}` |
| 方法 | GET |
| 参数 | fileId (路径参数) |
| 返回 | m3u8索引文件流 |

### 12.7 获取视频ts分片
| 项目 | 内容 |
|-----|------|
| 接口 | `/file/videoResource/{fileId}/{ts}` |
| 方法 | GET |
| 参数 | fileId, ts (路径参数) |
| 返回 | ts视频分片流 |

---

## 十三、系统设置 (sysSetting)

### 13.1 获取系统配置
| 项目 | 内容 |
|-----|------|
| 接口 | `/sysSetting/getSetting` |
| 方法 | POST |
| 参数 | 无 |
| 返回 | `{code: 1, data: SysSettingDto}` |

---

## 十四、数据结构定义

### VideoInfo (视频信息)
```json
{
  "videoId": "视频ID",
  "title": "视频标题",
  "description": "视频简介",
  "cover": "封面路径",
  "categoryId": 分类ID,
  "tags": "标签",
  "userId": "发布者ID",
  "playCount": 播放数,
  "likeCount": 点赞数,
  "coinCount": 投币数,
  "collectCount": 收藏数,
  "duration": 时长(秒),
  "createTime": "创建时间"
}
```

### VideoInfoResultVO (视频详情返回)
```json
{
  "videoId": "视频ID",
  "title": "视频标题",
  "description": "视频简介",
  "cover": "封面路径",
  "categoryId": 分类ID,
  "categoryName": "分类名称",
  "tags": "标签",
  "userId": "发布者ID",
  "nickname": "发布者昵称",
  "avatar": "发布者头像",
  "playCount": 播放数,
  "likeCount": 点赞数,
  "coinCount": 投币数,
  "collectCount": 收藏数,
  "duration": 时长(秒),
  "createTime": "创建时间",
  "videoType": 视频类型(0单p/1多p),
  "fileId": "文件ID"
}
```

### VideoInfoFile (视频文件)
```json
{
  "fileId": "文件ID",
  "videoId": "视频ID",
  "fileIndex": 文件序号,
  "fileUrl": "文件路径"
}
```

### VideoComment (评论)
```json
{
  "commentId": 评论ID,
  "videoId": "视频ID",
  "userId": "用户ID",
  "nickname": "用户昵称",
  "avatar": "用户头像",
  "content": "评论内容",
  "pCommentId": 父评论ID,
  "replyUserId": "回复用户ID",
  "likeCount": 点赞数,
  "createTime": "创建时间"
}
```

### VideoDanmu (弹幕)
```json
{
  "danmuId": 弹幕ID,
  "videoId": "视频ID",
  "fileId": "文件ID",
  "userId": "用户ID",
  "content": "弹幕内容",
  "color": "颜色",
  "time": 出现时间(秒),
  "createTime": "创建时间"
}
```

### UserInfo (用户信息)
```json
{
  "userId": "用户ID",
  "nickname": "昵称",
  "avatar": "头像",
  "sex": 性别(0未知/1男/2女),
  "birthday": "生日",
  "likeCount": 获赞数,
  "followCount": 关注数,
  "fansCount": 粉丝数,
  "createTime": "注册时间"
}
```

### VideoSeries (视频系列)
```json
{
  "seriesId": 系列ID,
  "userId": "用户ID",
  "seriesName": "系列名称",
  "seriesCover": "系列封面",
  "createTime": "创建时间"
}
```

### UserAction (用户行为)
```json
{
  "actionId": 行为ID,
  "videoId": "视频ID",
  "userId": "用户ID",
  "actionType": 行为类型(0点赞/1踩/2投币/3收藏),
  "actionCount": 行为数量(投币时)
}
```

### PaginationResultVO (分页返回)
```json
{
  "list": [...],
  "total": 总数,
  "pageNo": 当前页,
  "pageSize": 每页大小
}
```

### SysSettingDto (系统设置)
```json
{
  "videoSize": 视频大小限制(MB),
  "imageSize": 图片大小限制(MB),
  "uploadSize": 上传大小限制(MB)
}
```
