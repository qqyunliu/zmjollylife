# JollyLife Video Platform

A full-stack video sharing and streaming platform (Bilibili-style) with multi-stage AI content moderation, HLS video streaming, danmaku (bullet comments), user social features, and an admin management backend.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Backend Architecture](#backend-architecture)
   - [Multi-Module Layout](#multi-module-layout)
   - [Database Schema](#database-schema)
   - [API Routes](#api-routes)
   - [Business Logic & Key Flows](#business-logic--key-flows)
   - [Request-Response Data Flow](#request-response-data-flow)
   - [Authentication & Authorization](#authentication--authorization)
   - [Video Storage & Streaming](#video-storage--streaming)
   - [Video Transcoding Pipeline](#video-transcoding-pipeline)
   - [AI Content Moderation](#ai-content-moderation)
   - [Redis as Multi-Purpose Infrastructure](#redis-as-multi-purpose-infrastructure)
5. [Frontend Architecture](#frontend-architecture)
   - [Two-App Layout](#two-app-layout)
   - [Routing](#routing)
   - [State Management (Pinia)](#state-management-pinia)
   - [API Request Layer](#api-request-layer)
   - [Video Player](#video-player)
   - [Reusable Components](#reusable-components)
6. [Key Design Decisions](#key-design-decisions)

---

## Project Overview

JollyLife (internally named "EasyLive") is a video sharing platform where users can register, upload videos, watch videos via HLS streaming with live danmaku, interact through likes/coins/collects/comments/follows, and manage their content through a user center. An admin backend provides dashboard analytics, video audit/review workflows, and content moderation tools.

The platform implements a complete video publishing pipeline: chunked resumable upload, FFmpeg-based transcoding to HLS, three-stage AI content moderation (visual frames, audio transcript, text), and a human review fallback. Videos only become publicly visible after passing all audit stages.

---

## Tech Stack

### Backend

| Component | Technology | Version |
|---|---|---|
| Language | Java | 8 |
| Framework | Spring Boot | 2.7.18 |
| Build Tool | Maven (multi-module) | — |
| ORM | MyBatis (mybatis-spring-boot-starter) | 2.3.1 |
| Database | MySQL (mysql-connector-java) | 8.0.23 driver / 9.4.0 server |
| Cache/Queue | Redis (spring-boot-starter-data-redis, Lettuce driver) | 2.7.18 |
| Search | Elasticsearch (spring-boot-starter-data-elasticsearch) | 3.3.2 (declared but not actively used in code) |
| Video Processing | FFmpeg (command-line via ProcessBuilder) | — |
| AI Content Audit | Volcengine (ByteDance) Ark API | — |
| Speech-to-Text | Faster-Whisper (Python, local, called via ProcessBuilder) | — |
| Captcha | easy-captcha | 1.6.2 |
| JSON | Fastjson + Jackson | 1.2.83 |
| Logging | Logback | 1.2.10 |
| Code Generation | Lombok | 1.18.22 |

### Frontend

| Component | Technology | Version |
|---|---|---|
| Framework | Vue 3 (Composition API, `<script setup>`) | ^3.4.21 |
| Build Tool | Vite | ^5.0.12 |
| Routing | Vue Router | ^4.3.0 |
| State Management | Pinia | ^2.1.7 |
| HTTP Client | Axios | ^1.7.2 |
| UI Library | Element Plus (zh-cn locale) | ^2.7.5 |
| Video Player | ArtPlayer + artplayer-plugin-danmuku | ^5.3.0 (web) / ^5.1.6 (admin) / ^5.1.4 (danmuku plugin) |
| HLS Streaming | hls.js | ^1.5.13 |
| Charts | ECharts (web) / Chart.js (admin) | ^5.4.2 / ^4.5.1 |
| Event Bus | mitt | ^3.0.1 |
| Date Handling | moment (zh-cn) | ^2.30.1 |
| Password Hashing | js-md5 (client-side) | ^0.8.3 |
| Cookie Management | vue-cookies | ^1.8.4 |
| Image Cropping | vue-cropper | ^1.1.3 |
| Drag-and-Drop | vue-draggable-plus | ^0.5.0 |
| Browser Fingerprinting | @fingerprintjs/fingerprintjs | ^4.4.3 |
| Styling | SCSS/Sass | 1.89.2 |

No TypeScript is used — the frontend is pure JavaScript with `.vue` SFCs.

### External Service Integrations

| Service | Provider | Purpose |
|---|---|---|
| Image/Frame Content Audit | Volcengine (ByteDance) Ark API | Multimodal vision model `doubao-seed-2-0-mini-260215` — audits extracted video frames for content safety |
| Text/Audio Content Audit | Volcengine (ByteDance) Ark API | Text model `deepseek-v3-2-251201` — audits video title, introduction, and audio transcript |
| Speech-to-Text | Faster-Whisper (Python, local) | Transcribes audio from video files for text-based content audit |
| Video Transcoding | FFmpeg (local) | Converts uploaded videos to HLS format (m3u8 + ts segments), extracts audio and video frames |
| Aliyun SDK | Alibaba Cloud (cn-shanghai) | Content safety SDK `green202203032` — dependency is commented out in `easylive-common/pom.xml`; not on classpath and not used in code |

---

## Project Structure

```
jollylife/
├── .env                          # Environment variables (DB, Aliyun, Volcengine secrets)
├── .env.example                  # Template for .env
├── .gitignore
├── workspace-java/               # Backend (Java/Spring Boot)
│   └── easylive-java/
│       ├── pom.xml               # Parent POM (aggregator)
│       ├── easylive.sql           # Full database schema + seed data
│       ├── easylive-common/       # Shared module: entities, mappers, services, utils
│       ├── easylive-admin/        # Admin backend API (port 7069, context-path /admin)
│       └── easylive-web/          # User-facing web API (port 7072)
└── workspace-web/
    └── jollylife-front/
        ├── jollylife-front-web/   # User-facing frontend (dev port 5000)
        └── jollylife-front-admin/  # Admin frontend (dev port 5001)
```

---

## Backend Architecture

### Multi-Module Layout

The backend is a Maven multi-module project with three modules. The parent POM (`com.easylive:easylive:1.0`) uses `packaging=pom` as an aggregator.

#### Module: `easylive-common` (shared core)

Contains all shared code used by both `easylive-admin` and `easylive-web`.

**Java package root: `com.easyjava`**

| Package | Contents |
|---|---|
| `entity/po/` | 17 Persistent Objects mapping 1:1 to database tables (e.g., `VideoInfo`, `VideoInfoPost`, `Info` (user_info), `VideoComment`, `VideoDanmu`, `VideoSeries`, `PlayHistory`, `UserFocus`, `UserMessage`, `UserAction`, `AuditConfig`, `CategoryInfo`, etc.) |
| `entity/dto/` | Data Transfer Objects: `TokenUserInfoDto` (auth token payload), `SysSettingDto` (system config), `UploadingFileDto` (upload state), `AiAuditResult` (AI audit result) |
| `entity/query/` | Query/filter parameter objects for MyBatis — one per PO, all extend `BaseQuery` (contains `SimplePage` for pagination) |
| `entity/vo/` | View Objects: `ResponseVO<T>` (unified API response), `PaginationResultVO<T>`, `VideoInfoResultVO`, `VideoStatusCountInfoVO` |
| `entity/constants/` | Constants (e.g., Redis key prefixes) |
| `entity/config/` | `AppConfig` — application configuration bean |
| `mappers/` | 17 MyBatis `@Mapper` interfaces + corresponding XML mapper files in `resources/com.easyjava/mappers/` |
| `service/` + `service/impl/` | 16 service interfaces and implementations (e.g., `VideoInfoPostServiceImpl`, `OpenAiAuditServiceImpl`, `RedisComponent`, etc.) |
| `enums/` | `VideoStatusEnum`, `UserStatusEnum`, `ResponseCodeEnum`, etc. |
| `utlis/` (sic) | `FFmpegUtils`, `StringTools`, `JsonUtils`, `DateUtils`, `CopyTools`, `ProcessUtils`, `TokenContext` (ThreadLocal for auth context) |
| `redis/` | `RedisConfig` (Redis connection configuration), `RedisUtils` |
| `component/` | `RedisComponent` — wraps Redis operations for sessions, queues, captcha, upload state, system settings |
| `config/` | `DotEnvPostProcessor` — custom Spring processor that loads `.env` file into Spring environment |
| `controller/` | `AGlobalExceptionHandlerController` — `@ControllerAdvice` global exception handler |

#### Module: `easylive-web` (user-facing API, port 7072)

**Java package root: `com.easyjava.web`**

| Package | Contents |
|---|---|
| `controller/` | `VideoController`, `VideoCommentController`, `VideoDanmuController`, `VideoSeriesController`, `AccountController`, `UcenterController`, `UcenterVideoPostController`, `UserActionController`, `UserFocusController`, `UserMessageController`, `PlayHistoryController`, `SysSettingController`, `FileController`, `categoryController` |
| `task/` | `ExecuteQueueTask` — background thread pool for video transcoding (polls Redis queue) |

Entry point: `EasyjavaWebRunApplication.java` (Spring Boot main class).

#### Module: `easylive-admin` (admin API, port 7069, context-path `/admin`)

**Java package root: `com.easyjava.admin`**

| Package | Contents |
|---|---|
| `controller/` | `AccountController`, `CategoryInfoController`, `categoryController`, `VideoInfoController`, `VideoManagementController`, `VideoAuditController`, `InteractController`, `UserController`, `FileController`, `IndexController` |
| `interceptor/` | `AppInterceptor` (auth interceptor for admin endpoints — exists as `@Component` but note: `WebAppConfigurer.addInterceptors` calls only `super.addInterceptors(registry)` and does not register the interceptor, so admin endpoints are currently unprotected), `WebAppConfigurer` |

Entry point: `EasyliveAdminRunApplication.java` (Spring Boot main class).

### Database Schema

Database: `easylive` on MySQL (charset `utf8mb4`), connection `localhost:3310`. The full schema is in `easylive.sql`.

#### Table: `user_info` (entity: `Info.java`)

User account table. The entity class is named `Info` but maps to the `user_info` table.

| Column | Type | Description |
|---|---|---|
| user_id | varchar(10) PK | User ID (random 10-char string) |
| nick_id | varchar(20) UNIQUE | Nickname |
| avatar | varchar(100) | Avatar image path |
| email | varchar(150) UNIQUE | Email (login key) |
| password | varchar(50) | MD5-hashed password |
| sex | tinyint | 0:female, 1:male, 2:unknown |
| birthday | varchar(10) | Birthday |
| school | varchar(150) | School |
| person_introduction | varchar(200) | Bio |
| join_time | datetime | Registration time |
| last_login_time | datetime | Last login time |
| last_login_ip | varchar(15) | Last login IP |
| status | tinyint | 0:disabled, 1:active, -1:deactivated |
| notice_info | varchar(300) | Space notice |
| total_coin_count | int | Total coins earned |
| current_coin_count | int | Current coin balance |
| theme | tinyint | UI theme preference |

#### Table: `tb_category_info` (entity: `CategoryInfo.java`)

Hierarchical video categories with parent-child relationship via `p_category_id`.

| Column | Type | Description |
|---|---|---|
| category_id | int PK AUTO_INCREMENT | Category ID |
| category_code | varchar(30) UNIQUE | Code (e.g., "java", "game") |
| category_name | varchar(30) | Display name |
| p_category_id | int | Parent category (0 = top-level) |
| icon | varchar(50) | Icon |
| background | varchar(50) | Background image |
| sort | tinyint | Sort order |

Pre-seeded top-level categories: JAVA, Girls, Tech, Life, Game. Sub-categories include Fashion/Beauty/Style/Trend under Girls, Mobile/Computer/Digital/AI/Internet under Tech, Food/Travel/Health/Home/Fitness under Life, MobileGame/PcGame/Esports under Game.

#### Table: `tb_video_info` (entity: `VideoInfo.java`)

Published videos visible to users. Only videos that passed all audit stages (status=3 in post table) are copied here.

| Column | Type | Description |
|---|---|---|
| video_id | varchar(18) PK | Video ID |
| video_cover | varchar(50) | Cover image path |
| video_name | varchar(100) | Title |
| user_id | varchar(10) | Uploader ID |
| create_time | datetime | Created time |
| last_update_time | datetime | Last updated |
| p_category_id | int | Parent category |
| category_id | int | Sub category |
| post_type | tinyint | 0:original, 1:repost |
| origin_info | varchar(200) | Source info (for reposts) |
| tags | varchar(300) | Tags |
| introduction | varchar(2000) | Description |
| interaction | varchar(5) | Interaction settings |
| duration | int | Duration in seconds |
| play_count | int | Play count (denormalized) |
| like_count | int | Like count (denormalized) |
| danku_count | int | Danmaku count (denormalized) |
| comment_count | int | Comment count (denormalized) |
| coin_count | int | Coin count (denormalized) |
| collect_count | int | Collect count (denormalized) |
| recommend_type | tinyint | 0:not recommended, 1:recommended |
| last_play_time | datetime | Last play time |

#### Table: `tb_video_info_post` (entity: `VideoInfoPost.java`)

Video submission staging table. Videos go through transcoding and AI audit here; approved videos are copied to `tb_video_info`.

| Column | Type | Description |
|---|---|---|
| video_id | varchar(10) PK | Video ID |
| video_cover | varchar(50) | Cover |
| video_name | varchar(100) | Title |
| user_id | varchar(10) | Uploader |
| create_time | datetime | Created |
| last_update_time | datetime | Updated |
| p_category_id | int | Parent category |
| category_id | int | Sub category |
| status | tinyint | 0:transcoding, 1:transcode-failed, 2:pending-audit, 3:approved, 4:rejected, 5:pending-manual-review |
| post_type | tinyint | 0:original, 1:repost |
| origin_info | varchar(200) | Source |
| tags | varchar(300) | Tags |
| introduction | varchar(2000) | Description |
| interaction | varchar(5) | Interaction settings |
| duration | int | Duration |
| ai_audit_status | tinyint | 0:pending, 1:pass, 2:fail |
| ai_audit_result | text | JSON detail of AI audit |
| ai_audit_time | datetime | AI audit time |
| ai_audit_reason | varchar(500) | AI reject reason |
| review_status | tinyint | 0:pending, 1:pass, 2:fail |
| review_time | datetime | Manual review time |
| reviewer_id | varchar(50) | Reviewer ID |
| review_reason | varchar(500) | Manual reject reason |

#### Table: `tb_video_info_file` (entity: `VideoInfoFile.java`)

Video file segments for published videos. Supports multi-part/P-series videos.

| Column | Type | Description |
|---|---|---|
| file_id | varchar(20) PK | Unique file ID |
| user_id | varchar(10) | Owner |
| video_id | varchar(10) | Video ID |
| file_name | varchar(200) | Original filename |
| file_index | int | Episode/part index |
| file_size | bigint | Size in bytes |
| file_path | varchar(100) | HLS directory path |
| duration | int | Duration in seconds |

#### Table: `tb_video_info_file_post` (entity: `VideoInfoFilePost.java`)

Video file segments for submissions (staging, pre-audit).

| Column | Type | Description |
|---|---|---|
| file_id | varchar(20) PK | File ID |
| upload_id | varchar(30) | Upload session ID |
| user_id | varchar(10) | User |
| video_id | varchar(18) | Video |
| file_index | int | Part index |
| file_name | varchar(200) | Name |
| file_size | bigint | Size |
| file_path | varchar(100) | Path |
| update_time | datetime | Updated |
| transfer_result | tinyint | 0:no-update, 1:has-update |
| duration | int | Duration |
| post_type | tinyint | 0:transcoding, 1:success, 2:fail |

#### Table: `video_comment` (entity: `VideoComment.java`)

Nested comments with parent-child relationship via `p_comment_id`.

| Column | Type | Description |
|---|---|---|
| comment_id | int PK AUTO_INCREMENT | Comment ID |
| p_comment_id | int | Parent comment (0 = top-level) |
| video_id | varchar(20) | Video |
| video_user_id | varchar(18) | Video owner |
| content | varchar(500) | Comment text |
| img_path | varchar(150) | Image |
| user_id | varchar(15) | Commenter |
| reply_user_id | varchar(15) | Replied-to user |
| top_type | tinyint | 0:normal, 1:pinned |
| post_time | datetime | Post time |
| like_count | int | Likes |
| hate_count | int | Dislikes |
| status | tinyint | 0:normal, -1:admin-deleted |

#### Table: `video_comment_audit`

AI audit records for comments (no entity class — audit log only in SQL).

| Column | Type | Description |
|---|---|---|
| audit_id | bigint PK | Audit ID |
| comment_id | int UNIQUE | Comment ID |
| video_id | varchar(20) | Video |
| user_id | varchar(15) | Commenter |
| p_comment_id | int | Parent |
| reply_user_id | varchar(15) | Reply target |
| content | varchar(500) | Content |
| audit_status | tinyint | 1:pass, 2:fail |
| audit_reason | varchar(200) | Reason |
| ai_model | varchar(50) | Model used |
| audit_time | datetime | Time |

#### Table: `video_danmu` (entity: `VideoDanmu.java`)

Danmaku (bullet comments) overlaid on videos during playback.

| Column | Type | Description |
|---|---|---|
| danmu_id | int PK AUTO_INCREMENT | Danmu ID |
| video_id | varchar(20) | Video |
| file_id | varchar(20) | File |
| user_id | varchar(15) | User |
| post_time | datetime | Post time |
| content | varchar(500) | Text |
| is_show | tinyint | Show setting (0:hidden, 1:visible) |
| color | varchar(10) | Color hex |
| time | int | Display time in seconds |

#### Table: `user_action` (entity: `UserAction.java`)

User interactions: video likes, coins, collects. Note: comment likes/hates are handled separately on the `video_comment` table (incrementing `like_count` / `hate_count` columns directly), not through this table.

| Column | Type | Description |
|---|---|---|
| action_id | int PK AUTO_INCREMENT | Action ID |
| video_id | varchar(20) | Video |
| video_user_id | varchar(18) | Video owner |
| comment_id | int | Comment (0 if video-level) |
| action_type | tinyint | SQL schema comment defines: 0:comment-like, 1:comment-hate, 2:video-like, 3:video-collect, 4:video-coin. However, actual code behavior differs: 0=video-like (updates `like_count`), 1=undefined toggle (no count update), 2=not handled in code, 3=video-collect (updates `collect_count`), 4=video-coin (updates `coin_count`) |
| user_id | varchar(15) | Actor |
| action_time | datetime | Time |

Unique constraint: `(video_id, comment_id, action_type, user_id)` — prevents duplicate actions.

#### Table: `user_focus` (entity: `UserFocus.java`)

Follow relationships between users.

| Column | Type | Description |
|---|---|---|
| id | int PK AUTO_INCREMENT | ID |
| user_id | varchar(15) | Follower |
| focus_user_id | varchar(15) | Followed user |
| create_time | datetime | Time |

#### Table: `user_message` (entity: `UserMessage.java`)

Notification messages.

| Column | Type | Description |
|---|---|---|
| message_id | int PK AUTO_INCREMENT | ID |
| user_id | varchar(15) | Recipient |
| message_type | tinyint | 1:like, 2:coin, 3:collect, 4:follow, 5:comment, 6:system |
| from_user_id | varchar(15) | Sender |
| video_id | varchar(20) | Video |
| comment_id | int | Comment |
| message_content | varchar(200) | Content |
| is_read | tinyint | 0:unread, 1:read |
| create_time | datetime | Time |

#### Table: `play_history` (entity: `PlayHistory.java`)

| Column | Type | Description |
|---|---|---|
| id | int PK AUTO_INCREMENT | ID |
| video_id | varchar(20) | Video |
| user_id | varchar(15) | User |
| play_time | datetime | Play time |
| progress | int | Progress in seconds |

#### Table: `search_keyword` (entity: `SearchKeyword.java`)

| Column | Type | Description |
|---|---|---|
| id | int PK AUTO_INCREMENT | ID |
| keyword | varchar(50) UNIQUE | Keyword |
| search_count | int | Search count |
| create_time | datetime | Created |

#### Table: `video_series` (entity: `VideoSeries.java`)

User-created video collections/series.

| Column | Type | Description |
|---|---|---|
| series_id | int PK AUTO_INCREMENT | Series ID |
| user_id | varchar(15) | Owner |
| series_name | varchar(50) | Name |
| series_cover | varchar(100) | Cover |
| create_time | datetime | Created |
| update_time | datetime | Updated |

#### Table: `video_series_video` (entity: `VideoSeriesVideo.java`)

Many-to-many join table between series and videos.

| Column | Type | Description |
|---|---|---|
| id | int PK AUTO_INCREMENT | ID |
| series_id | int | Series |
| video_id | varchar(20) | Video |
| sort | int | Sort order |

#### Table: `tb_audit_config` (entity: `AuditConfig.java`)

AI audit provider configuration stored as key-value pairs.

| Column | Type | Description |
|---|---|---|
| id | int PK | ID |
| config_key | varchar(100) UNIQUE | Key |
| config_value | text | Value |
| description | varchar(200) | Description |
| create_time | datetime | Created |
| update_time | datetime | Updated |

#### Key Relationships

- `user_info` 1:N `tb_video_info` via `user_id`
- `tb_video_info` 1:N `tb_video_info_file` via `video_id`
- `tb_video_info` 1:N `video_comment` via `video_id`
- `video_comment` self-referential via `p_comment_id` (threaded replies)
- `tb_video_info` 1:N `video_danmu` via `video_id` / `file_id`
- `user_info` 1:N `user_action` N:1 `tb_video_info` (many-to-many interaction)
- `user_info` 1:N `user_focus` N:1 `user_info` (self-referential follow)
- `user_info` 1:N `user_message` (notifications)
- `user_info` 1:N `play_history` N:1 `tb_video_info`
- `video_series` 1:N `video_series_video` N:1 `tb_video_info` (series membership)
- `tb_video_info_post` is a staging table; approved videos are **copied** (not moved) to `tb_video_info`

### API Routes

All routes use `@RequestMapping` (accepts all HTTP methods). Two separate Spring Boot applications run on different ports.

#### Web API (port 7072, no context-path)

**AccountController (`/account`)**

| Path | Description |
|---|---|
| `/account/checkCode` | Get arithmetic captcha image (base64) + key |
| `/account/register` | Register with email, nickname, password, captcha |
| `/account/login` | Login with email/password/captcha, returns token in cookie |
| `/account/autoLogin` | Auto-login via existing token cookie |
| `/account/logout` | Clear token cookie and Redis session |

**VideoController (`/video`)**

| Path | Description |
|---|---|
| `/video/loadRecommendVideo` | Paginated recommended video list |
| `/video/loadVideo` | Paginated videos by category |
| `/video/getVideoInfo` | Get single video detail, increments play count, shows user's like/coin/collect status |
| `/video/loadVideoPList` | Get all file parts (P-series) for a video |
| `/video/reportVideoPlayOnline` | Report online play (stub) |
| `/video/search` | Search videos by keyword, records search keyword |
| `/video/getSearchKeywordTop` | Get top 10 hot search keywords |
| `/video/getVideoRecommend` | Get recommended videos (list, no pagination) |
| `/video/loadHotVideoList` | Get top 20 hot videos by play count |

**FileController (`/file`)**

| Path | Description |
|---|---|
| `/file/getResource` | Serve static image files (covers, avatars) with 30-day cache |
| `/file/preUploadVideo` | Pre-upload: generate uploadId, store metadata in Redis |
| `/file/uploadVideo` | Upload video chunk (multipart), supports resume |
| `/file/delUploadVideo` | Cancel upload, delete temp chunks |
| `/file/UploadImage` | Upload image, optionally generate thumbnail |
| `/file/videoResource/{fileId}` | Serve HLS m3u8 index file for video playback |
| `/file/videoResource/{fileId}/{ts}` | Serve HLS .ts segment file |

**VideoCommentController (`/comment`)**

| Path | Description |
|---|---|
| `/comment/loadComment` | Paginated comments for a video |
| `/comment/loadReply` | Get replies for a parent comment |
| `/comment/postComment` | Post a comment (requires login) |
| `/comment/userDelComment` | Delete own comment |
| `/comment/likeComment` | Like a comment |
| `/comment/hateComment` | Dislike a comment |
| `/comment/topComment` | Pin a comment |
| `/comment/cancelTopComment` | Unpin a comment |

**VideoDanmuController (`/danmu`)**

| Path | Description |
|---|---|
| `/danmu/loadDanmu` | Load danmaku list by videoId or fileId |
| `/danmu/postDanmu` | Post danmaku (requires login) |
| `/danmu/delDanmu` | Delete danmaku |

**UserActionController (`/userAction`)**

| Path | Description |
|---|---|
| `/userAction/doAction` | Perform action (like/coin/collect) on video |
| `/userAction/getUserAction` | Check if user has performed an action |
| `/userAction/getVideoActionCount` | Get like/coin/collect counts for a video |

**PlayHistoryController (`/history`)**

| Path | Description |
|---|---|
| `/history/loadHistory` | Load play history for current user |
| `/history/delHistory` | Delete a single history entry |
| `/history/cleanHistory` | Clear all play history |

**UserFocusController (`/uhome`)**

| Path | Description |
|---|---|
| `/uhome/focus` | Follow a user |
| `/uhome/cancelFocus` | Unfollow a user |
| `/uhome/loadFocusList` | List users I follow |
| `/uhome/loadFansList` | List my followers |

**UserMessageController (`/message`)**

| Path | Description |
|---|---|
| `/message/loadMessage` | Load all messages for current user |
| `/message/getNoReadCount` | Get unread message count |
| `/message/getNoReadCountGroup` | Get unread counts grouped by type |
| `/message/readAll` | Mark all messages as read |
| `/message/delMessage` | Delete a message |

**UcenterController (`/uhome`)**

| Path | Description |
|---|---|
| `/uhome/getUserInfo` | Get user profile + focus/fans counts |
| `/uhome/updateUserInfo` | Update profile (nickName/signature go through AI audit) |
| `/uhome/loadVideoList` | Load user's video submissions (filtered by status) |
| `/uhome/loadUserCollection` | Load user's collected videos |
| `/uhome/saveTheme` | Save UI theme preference (stub) |

**UcenterVideoPostController (`/ucenter`)**

| Path | Description |
|---|---|
| `/ucenter/postVideo` | Submit/edit a video post (creates entry in `tb_video_info_post`) |
| `/ucenter/loadVideoList` | List user's submitted videos with status filtering |
| `/ucenter/getVideoCountInfo` | Count of approved/rejected/in-progress videos |
| `/ucenter/loadAllVideo` | List all approved videos by user |
| `/ucenter/loadComment` | Paginated comments by current user |
| `/ucenter/delComment` | Delete own comment |
| `/ucenter/loadDanmu` | Paginated danmaku by current user |
| `/ucenter/delDanmu` | Delete own danmaku |
| `/ucenter/getActualTimeStatisticsInfo` | Real-time stats: video count, play count, fans |
| `/ucenter/getWeekStatisticsInfo` | Weekly stats (stub) |
| `/ucenter/deleteVideo` | Delete a video (owner only, cascading delete) |
| `/ucenter/getVideoByVideoId` | Get video info by ID |
| `/ucenter/saveVideoInteraction` | Save interaction settings (stub) |

**VideoSeriesController (`/uhome/series`)**

| Path | Description |
|---|---|
| `/uhome/series/loadVideoSeries` | List user's video series |
| `/uhome/series/saveVideoSeries` | Create/update a series |
| `/uhome/series/changeVideoSeriesSort` | Change video sort within a series |
| `/uhome/series/getVideoSeriesDetail` | Get series detail |
| `/uhome/series/delVideoSeries` | Delete a series |
| `/uhome/series/saveSeriesVideo` | Add video to series |
| `/uhome/series/delSeriesVideo` | Remove video from series |
| `/uhome/series/loadAllVideo` | List videos in a series |
| `/uhome/series/loadVideoSeriesWithVideo` | List series with their videos |

**categoryController (`/category`)**

| Path | Description |
|---|---|
| `/category/loadCategory` | Load all categories (tree structure) |

**SysSettingController (`/sysSetting`)**

| Path | Description |
|---|---|
| `/sysSetting/getSetting` | Get system settings from Redis |

#### Admin API (port 7069, context-path `/admin`)

**AccountController**

| Path | Description |
|---|---|
| `/admin/checkCode` | Admin captcha |
| `/admin/login` | Admin login (account from config, MD5 password) |
| `/admin/logout` | Admin logout |

**IndexController (`/index`)**

| Path | Description |
|---|---|
| `/admin/index/getActualTimaStatisticsInfo` | Dashboard: user count, play count, like count, comment count |
| `/admin/index/getWeekStatisticsInfo` | New user registrations over past 7 days |

**UserController (`/user`)**

| Path | Description |
|---|---|
| `/admin/user/loadUser` | Paginated user list |
| `/admin/user/changeStatus` | Change user status (disable/enable/deactivate) |

**VideoAuditController (`/video`)**

| Path | Description |
|---|---|
| `/admin/video/audit` | Trigger AI audit for a specific video |
| `/admin/video/review/list` | List videos pending/all audit statuses |
| `/admin/video/review` | Manual review action (pass/reject) |
| `/admin/video/review/detail` | Get review detail with play file ID |
| `/admin/video/stats` | Audit statistics (pending/passed/rejected/review counts) |

**VideoManagementController (`/video`)**

| Path | Description |
|---|---|
| `/admin/video/management/list` | List all published videos |
| `/admin/video/management/recommend` | Set video recommendation status |
| `/admin/video/management/delete` | Delete video (admin) |
| `/admin/video/management/stats` | Video statistics (total/recommended counts) |

**VideoInfoController (`/videoInfo`)**

| Path | Description |
|---|---|
| `/admin/videoInfo/loadVideoList` | List all video posts |
| `/admin/videoInfo/auditVideo` | Audit a video (set status) |

**CategoryInfoController (`/categoryInfo`)**

| Path | Description |
|---|---|
| `/admin/categoryInfo/loadDataList` | Paginated category list |
| `/admin/categoryInfo/add` | Add category |
| `/admin/categoryInfo/addBatch` | Batch add |
| `/admin/categoryInfo/addOrUpdateBatch` | Batch add/update |
| `/admin/categoryInfo/getCategoryInfoByCategoryId` | Get by ID |
| `/admin/categoryInfo/updateCategoryInfoByCategoryId` | Update by ID |
| `/admin/categoryInfo/deleteCategoryInfoByCategoryId` | Delete by ID |
| `/admin/categoryInfo/getCategoryInfoByCategoryCode` | Get by code |
| `/admin/categoryInfo/updateCategoryInfoByCategoryCode` | Update by code |
| `/admin/categoryInfo/deleteCategoryInfoByCategoryCode` | Delete by code |

**InteractController (`/interact`)**

| Path | Description |
|---|---|
| `/admin/interact/loadComment` | Paginated comment list (admin view) |
| `/admin/interact/delComment` | Soft-delete comment (status -> -1) |
| `/admin/interact/loadDanmu` | Paginated danmaku list (admin view) |
| `/admin/interact/delDanmu` | Soft-delete danmaku (is_show -> 0) |

**FileController (`/file`)**

| Path | Description |
|---|---|
| `/admin/file/uploadImage` | Upload image with optional thumbnail |
| `/admin/file/getResource` | Serve static files |
| `/admin/file/videoResourcePost/{fileId}` | Serve HLS m3u8 for post videos (pre-publish preview) |
| `/admin/file/videoResourcePost/{fileId}/index.m3u8` | Serve m3u8 directly |
| `/admin/file/videoResourcePost/{fileId}/{ts}` | Serve .ts segment for post videos |

**categoryController (`/category`)**

| Path | Description |
|---|---|
| `/admin/category/loadCategory` | Load all categories |
| `/admin/category/saveCategory` | Save (create or update) a category |
| `/admin/category/deleteCategory` | Delete a category |
| `/admin/category/changeSort` | Change category sort order |

### Business Logic & Key Flows

#### User Registration & Authentication

Registration: User submits email, nickname, MD5-hashed password (hashed client-side via js-md5), and captcha. Backend validates captcha from Redis, checks for duplicate email/nickname, generates a random 10-char user ID, grants 10 initial coins, sets status=1 (active), and inserts into `user_info`. The server applies an **additional MD5 hash** on the received password via `StringTools.encodeByMd5()` before storing — so the stored value is a double-MD5 hash (client MD5, then server MD5).

Login: User submits email + password (MD5) + captcha. Backend validates captcha from Redis, verifies credentials by direct string comparison (`info.getPassword().equals(password)` — no additional server-side hashing on login), generates a UUID token, stores a `TokenUserInfoDto` (containing userId, nickId, avatar, fansCount, currentCoinCount, focusCount, expireAt) in Redis with a 7-day TTL, and sets the token as an HTTP cookie (`token`, 7-day max-age, path `/`).

Auto-login: Reads token from the request header. If found in Redis, checks the remaining TTL: if less than 1 day remains, generates a new UUID token with a fresh 7-day TTL (old token remains in Redis). If more than 1 day remains, reuses the existing token. Re-saves the token to the HTTP cookie and returns user info to the frontend.

Logout: Calls `cleanCookie()` which attempts to delete a Redis key using the prefix `adminToken` + token — this does not match the actual web token key prefix (`easylivetoken:web:`), so the token is **not actually deleted** from Redis. The HTTP cookie is expired correctly.

#### Video Upload & Publishing Pipeline

This is the most complex flow, spanning multiple controllers and a background task:

1. **Pre-upload** (`/file/preUploadVideo`): Frontend requests an `uploadId`. Backend creates an `UploadingFileDto` in Redis (1-day TTL) tracking upload progress and creates a temp directory `{projectFolder}/file/temp/{date}/{userId}{uploadId}/`.

2. **Chunked upload** (`/file/uploadVideo`): Frontend splits the video file into 512KB chunks and uploads each chunk sequentially via multipart. Each chunk is saved as a file named by `chunkIndex` in the temp directory. Redis tracks `chunkIndex` and cumulative `fileSize`. Supports resume — validates that `chunkIndex` must be `lastChunkIndex + 1` or `lastChunkIndex` (re-upload current).

3. **Publish video** (`/ucenter/postVideo`): Frontend submits video metadata (title, cover, category, tags, introduction, interaction settings, file list) as JSON. Backend:
   - Generates a `videoId` (random 10-char string) for new videos
   - Inserts into `tb_video_info_post` with status=0 (transcoding)
   - Inserts/updates file records in `tb_video_info_file_post`
   - Pushes new file IDs to a Redis transfer queue (`easylivequeue:transfer`)

4. **Background transcoding** (`ExecuteQueueTask`): A `@PostConstruct` thread pool (`Executors.newFixedThreadPool(2)` capacity, but only 1 thread actively used) continuously polls the Redis transfer queue. For each file:
   - Copies the temp directory to a permanent `video/{date}/{userId}{uploadId}/` location
   - Merges chunks into `temp.mp4` (using `RandomAccessFile` byte concatenation)
   - Extracts audio to `audio.mp3` via FFmpeg
   - Checks video codec; if HEVC/H.265, converts to H.264 first
   - Converts to HLS: generates `index.m3u8` + `*.ts` segments (10-second segments) via FFmpeg
   - Updates `tb_video_info_file_post` with duration, file size, path, transfer result
   - When ALL files for a video are transcoded, sets video status to STATUS2 (pending audit)

5. **AI content audit** (automatic after transcoding completes): Three-stage audit — see [AI Content Moderation](#ai-content-moderation). If all pass: status -> STATUS3 (approved), data is copied to `tb_video_info` (becomes publicly visible). If any fail: status -> STATUS5 (pending manual review), stores AI audit reason.

6. **Manual review** (admin): Admin can pass (status=3, triggers `transferToVideoInfo`) or reject (status=4, stores reason) via `/admin/video/review`.

#### Video Deletion

Owner-only deletion (or admin). Deletes physical video files from disk (HLS directories), then cascading deletes from: `tb_video_info_file_post`, `tb_video_info_file`, `video_series_video`, `video_comment`, `video_danmu`, `user_action`, `play_history`, `user_message`, `tb_video_info`, `tb_video_info_post`. Wrapped in `@Transactional`.

#### Comment System

Threaded comments with parent-child relationship via `p_comment_id`. Supports pinning (`top_type`), likes/hates (handled directly by incrementing `like_count` / `hate_count` columns on the `video_comment` row — not tracked in the `user_action` table). Soft delete (status -> -1) for admin deletions.

#### User Interactions

Actions: video-like (type 0, updates `like_count`), undefined toggle (type 1, inserts/deletes a `UserAction` record but does not update any counter), video-collect (type 3, updates `collect_count`), video-coin (type 4, updates `coin_count`). Note: action_type 2 is defined in the SQL schema comment but not handled in code. Unique constraint on `(video_id, comment_id, action_type, user_id)` prevents duplicate actions. Coins are **not** deducted from the user's balance or credited to the video owner — the code only inserts a `UserAction` record and increments the video's `coin_count` counter.

#### Search

Keyword search on `tb_video_info` (MyBatis LIKE query in mapper XML). Records search keywords in `search_keyword` table with search count. Hot keywords: top 10 by search count.

### Request-Response Data Flow

```
HTTP Request
  |
  v
[Interceptor] -- Admin: AppInterceptor checks admin token in header/cookie
                 Web: No interceptor (token checked per-endpoint via ABaseController.getTokenUserInfoDto())
  |
  v
[Controller] -- @RestController, @RequestMapping
                 Extends ABaseController (provides getSuccessResponseVO, getErrorResponseVO, getTokenUserInfoDto, getIpAddr)
                 Validates input with @NotEmpty, @NotNull, @Validated
  |
  v
[Service] -- @Service, business logic
              @Transactional for write operations
              Uses RedisComponent for caching/queue operations
              Uses FFmpegUtils for video processing
  |
  v
[Mapper] -- MyBatis @Mapper interface
  |
  v
[XML Mapper] -- SQL in src/main/resources/com.easyjava/mappers/*.xml
  |
  v
[MySQL] -- database "easylive"
```

**Unified response format** — all API responses are wrapped in `ResponseVO<T>`:

```json
{
  "status": "success" | "error",
  "code": 200 | 404 | 500 | 600 | 601 | 901,
  "info": "message",
  "data": "<T>"
}
```

**Response codes**: 200 = success, 404 = not found, 500 = server error, 600 = business error, 601 = duplicate key, 901 = not logged in / session expired.

**Global exception handling**: `AGlobalExceptionHandlerController` (`@ControllerAdvice`) catches all exceptions and converts to `ResponseVO`: `BusinessException` -> code 600, `NoHandlerFoundException` -> code 404, `ConstraintViolationException` -> code 600, `DuplicateKeyException` -> code 601, others -> code 500.

### Authentication & Authorization

#### Web Users (Token-based via Redis)

No Spring Security is used. Custom token-based authentication:

- **Token generation**: UUID, stored in Redis key `easylivetoken:web:{token}` with 7-day TTL. Note: the key prefix is `easylive` (from `Constans.REDIS_KEY_PREFIX`) concatenated with `token:web:`, producing `easylivetoken:web:` (no colon between "easylive" and "token").
- **Token payload** (`TokenUserInfoDto`): userId, nickId, avatar, token, expireAt, fansCount, currentCoinCount, focusCount
- **Token transport**: HTTP cookie named `token` (7-day max-age, path `/`)
- **Token retrieval**: Controllers call `ABaseController.getTokenUserInfoDto()` which reads token from `request.getHeader("token")` (header only, not cookie) and queries Redis. If found, sets `TokenContext` (ThreadLocal) for downstream use.
- **Auto-login**: Reads token from the request header. If found in Redis, checks the remaining TTL: if less than 1 day remains, generates a new UUID token with a fresh 7-day TTL and saves it to Redis (the old token remains). If more than 1 day remains, reuses the existing token. In both cases, the token is re-saved to the HTTP cookie and user info is returned to the frontend.
- **Logout**: Calls `cleanCookie()` which attempts to clean a Redis key using the prefix `adminToken` + token value — this does not match the actual web token key prefix (`easylivetoken:web:`), so the token is **not actually deleted** from Redis. The HTTP cookie is expired correctly.
- **No interceptor**: Web module has no `HandlerInterceptor`. Token is checked **per-endpoint** — endpoints that don't call `getTokenUserInfoDto()` work without auth.

#### Admin Users (Token-based via Redis)

- **Mechanism**: Same UUID-in-Redis approach, key prefix `easylivetoken:admin:`, 1-day TTL.
- **Interceptor**: `AppInterceptor` is designed to check all admin endpoints except `/account` (login/captcha). However, `WebAppConfigurer.addInterceptors` calls only `super.addInterceptors(registry)` and **never registers** `AppInterceptor`. This means all admin endpoints are currently unprotected — the interceptor logic exists but is not wired up. For file endpoints, the interceptor would read token from cookie; for API endpoints, from header `adminToken`.
- **Admin credentials**: Hardcoded in config (`admin.account=admin`, password from env `ADMIN_PASSWORD`). No database table for admins. Password compared as MD5 hash.
- **Logout**: Admin logout has the same token-not-deleted bug as web logout — `cleanCookie()` calls `cleanCheckCode()` which uses the prefix `adminToken` + token, not the correct `easylivetoken:admin:` prefix. The token is not actually deleted from Redis. Note: admin login does properly clean old tokens via `cleanToken4Admin()` using the correct key prefix.
- **Error**: Missing/invalid admin token would throw `BusinessException(CODE_901)` — but since the interceptor is not registered, this code path is never reached.

#### User Roles

- **Regular user**: Registered via email. Can upload videos, comment, interact, follow users.
- **Admin**: Single admin account from config. Can manage users, audit videos, manage categories, moderate comments/danmaku, view dashboard stats.
- **No RBAC**: Only two flat roles, no Spring Security or role-based access control hierarchy.

### Video Storage & Streaming

#### Storage

Videos are stored on the **local filesystem** (no cloud storage / S3 / OSS integration). Paths:

| Content | Path |
|---|---|
| Video HLS files | `{projectFolder}/file/video/{date}/{userId}{uploadId}/` — contains `index.m3u8` + `*.ts` segments + `audio.mp3` |
| Cover images | `{projectFolder}/file/cover/{date}/{randomString}.png` |
| Avatars | `{projectFolder}/file/avatar/` |

The `projectFolder` is configured in `application.yml` (default: `c:/webser/easylive/`).

#### Serving (HLS)

Videos are served via **HTTP Live Streaming (HLS)**, not direct file download:

1. Frontend requests `/file/videoResource/{fileId}` to get the `index.m3u8` HLS manifest
2. Browser/player parses the m3u8 manifest
3. Browser requests individual `.ts` segments via `/file/videoResource/{fileId}/{ts}`
4. Backend reads files from disk via `FileInputStream` with a 1KB buffer, streaming to `HttpServletResponse.getOutputStream()`

No range request support, no CDN. Admin preview for pre-publish videos uses `/admin/file/videoResourcePost/{fileId}/index.m3u8` (with m3u8 content normalization for path safety).

### Video Transcoding Pipeline

The `ExecuteQueueTask` class in `easylive-web` module starts a thread pool on `@PostConstruct` (`Executors.newFixedThreadPool(2)` capacity, but only 1 thread actively polls the queue — a second `@PostConstruct` method `consumFileQueue()` is commented out). It continuously polls the Redis transfer queue (`easylivequeue:transfer`). For each file:

1. **Merge chunks**: Concatenate uploaded chunks into `temp.mp4` using `RandomAccessFile` byte concatenation
2. **Extract audio**: Extract audio to `audio.mp3` via FFmpeg (used later for AI audio audit)
3. **Codec check**: If video codec is HEVC/H.265, re-encode to H.264 via `ffmpeg -c:v libx264 -crf 20`
4. **HLS conversion**: First tries `ffmpeg -codec copy -hls_time 10` (fast, stream copy). If that fails (no ts files generated), falls back to full re-encode: `ffmpeg -c:v libx264 -c:a aac -profile:v baseline -level 3.0 -hls_time 10`
5. **Cleanup**: Original merged `temp.mp4` is deleted after HLS generation
6. **Update database**: `tb_video_info_file_post` updated with duration, file size, path, transfer result
7. **Check completion**: When ALL files for a video are transcoded, video status is set to STATUS2 (pending audit)

### AI Content Moderation

Three-stage AI content audit runs automatically after transcoding completes, implemented in `OpenAiAuditServiceImpl`. The prompt design follows "innocent until proven guilty" — only clearly severe violations are blocked.

**Stage 1 — Visual frame audit**: Extracts 1 frame every 5 seconds (max 10 frames) via FFmpeg, sends each as base64-encoded JPEG to Volcengine Ark API with the `doubao-seed-2-0-mini-260215` vision model. Short-circuits on the first FAIL result.

**Stage 2 — Audio transcript audit**: If frames pass, extracts audio from the video, runs local Faster-Whisper (Python, `tiny` model, Chinese) for speech-to-text via `ProcessBuilder`. The resulting transcript is sent to the `deepseek-v3-2-251201` text model for content safety audit.

**Stage 3 — Text audit**: The video title and introduction text are sent to the `deepseek-v3-2-251201` model for content safety audit.

**Outcome**: If all three stages pass, video status -> STATUS3 (approved), and `transferToVideoInfo()` copies data from `tb_video_info_post` to `tb_video_info` (making it publicly visible). If any stage fails, video status -> STATUS5 (pending manual review), and the AI audit reason is stored.

**Admin review**: Admin can trigger manual AI audit via `/admin/video/audit`, review pending videos via `/admin/video/review/list`, and approve/reject via `/admin/video/review`.

### Redis as Multi-Purpose Infrastructure

Redis (port 6380, Lettuce driver) is used for far more than caching:

| Use Case | Key Pattern | TTL |
|---|---|---|
| Web user sessions | `easylivetoken:web:{token}` | 7 days |
| Admin user sessions | `easylivetoken:admin:{token}` | 1 day |
| Captcha storage | — | 10 minutes |
| Upload state tracking | `easyliveuploading:{userId}{uploadId}` (keyed by userId + uploadId) | 1 day |
| Video transcoding queue | `easylivequeue:transfer` (Redis list, lpush/rpop) | — |
| File deletion queue | `easylivefile:list:del:{videoId}` (Redis list) | 7 days |
| Category cache | — | — |
| System settings cache | `easylivesysSetting:` | — |

#### System Settings (cached in Redis)

The `SysSettingDto` contains runtime-configurable parameters:

| Setting | Default | Description |
|---|---|---|
| registerCoinCount | 10 | Coins given on registration |
| postVideoCoinCOUNT | 5 | Coins for posting a video |
| videoSize | 100 | Max video size in MB |
| videoCount | 10 | Max files per video |
| videoPCount | 10 | Max P-series parts |
| commentCount | 20 | Max comments per interaction |
| danmuCount | 20 | Max danmaku per interaction |

---

## Frontend Architecture

### Two-App Layout

The frontend consists of two separate Vue 3 applications:

| App | Path | Dev Port | Backend Proxy Target | Proxy Rewrite | Purpose |
|---|---|---|---|---|---|
| `jollylife-front-web` | `workspace-web/jollylife-front/jollylife-front-web/` | 5000 | `http://localhost:7072/` | `/api` -> `/` | User-facing video site |
| `jollylife-front-admin` | `workspace-web/jollylife-front/jollylife-front-admin/` | 5001 | `http://localhost:7069/` | `/api` -> `/admin` | Admin management dashboard |

Both apps share the same core tech stack (Vue 3 + Vite + Pinia + Element Plus + Axios) but have separate `package.json`, `vite.config.js`, and source directories. The path alias `@` maps to `./src` in both apps.

### Routing

#### User App Routes (`jollylife-front-web/src/router/index.js`)

Uses `createWebHistory`. **No navigation guards** — authentication is handled reactively via Pinia `loginStore.showLogin` (shows login dialog when server returns code 901).

| Path | Component | Description |
|---|---|---|
| `/` | `Layout.vue` | Main site shell (header + category bar + router-view + login dialog) |
| `/` (child) | `Index.vue` | Home page: carousel + recommended video grid + infinite-scroll list |
| `/v/:pCategoryCode` | `CategoryVideo.vue` | Category video list |
| `/v/:pCategoryCode/:categoryCode` | `CategoryVideo.vue` | Sub-category video list |
| `/video/:videoId` | `VideoDetail.vue` | Video player page with danmaku, comments, P-list |
| `/search` | `Search.vue` | Search results + hot keywords |
| `/message` | `Message.vue` | Notification messages |
| `/history` | `History.vue` | Play history with progress bars |
| `/user/:userId/collection` | `Collection.vue` | User's collected videos |
| `/user/:userId` | `UserHome.vue` | User homepage with follow/fans stats |
| `/ucenter` | `UcLayout.vue` | User center shell (redirects to `/ucenter/home`) |
| `/ucenter/home` | `Home.vue` | User center dashboard with real-time stats |
| `/ucenter/postVideo` | `Post.vue` | Upload video form |
| `/ucenter/editVideo` | `Post.vue` | Edit video (same component, reads `?videoId=` query) |
| `/ucenter/video` | `Video.vue` | Submission management |
| `/ucenter/comment` | `Comment.vue` | Comment management |
| `/ucenter/danmu` | `Danmu.vue` | Danmaku management |

#### Admin App Routes (`jollylife-front-admin/src/router/index.js`)

Uses `createWebHistory`. **No navigation guards.** Root `/` redirects to `/login`.

| Path | Component | Description |
|---|---|---|
| `/login` | `Account.vue` | Admin login page |
| `/home` | `Home.vue` | Dashboard: stat cards + 7-day user registration chart |
| `/content/video-audit` | `VideoAudit.vue` | Video audit/review with inline player preview |
| `/content/video` | `VideoManagement.vue` | Video management: search, recommend, delete |
| `/interact/comment` | `CommentManagement.vue` | Comment moderation |
| `/interact/delDanmu` | `DanmuManagement.vue` | Danmaku moderation |
| `/user/userList` | `UserManagement.vue` | User management: search, change status |

### State Management (Pinia)

#### User App Stores

| Store | State | Actions |
|---|---|---|
| `loginStore` | `showLogin` (controls login dialog), `userInfo` (current user data) | `setLogin(show)`, `saveUserInfo(Info)` |
| `categoryStore` | `categoryMap` (code -> category lookup), `categoryList` (top-level categories with children), `currentPCategory` | `saveCategoryMap`, `saveCategoryList`, `setCureentPCategory` |
| `navActionStore` | `fixedHeader`, `fixedCategory`, `showHeader`, `showCategory`, `forceFixedHeader` (booleans controlling header/category bar visibility) | setters for each |
| `sysSettingStore` | `sysSetting` (system settings like `videoPCount`, `videoSize`) | `saveSetting(data)` |

#### Admin App Stores

| Store | State | Actions |
|---|---|---|
| `loginStore` | `showLogin`, `userInfo` | `setLogin`, `saveUserInfo` |
| `categoryStore` | `categorieMap`, `categoriesList`, `currentPCategory` | `saveCategoryMap`, `saveCategoryList`, `setCurrentPCategory` |

#### Event Bus

A **mitt**-based event bus (`src/eventbus/eventBus.js`) with named event constants: `VIDEO_UPLOAD_START/PROGRESS/SUCCESS/ERROR`, `USER_LOGIN/LOGOUT`, `NOTIFICATION_SHOW`, `MODAL_OPEN/CLOSE`. In practice, custom events like `changeF` (switch video file in player), `playEnd` (video ended), `loadDanmu`, `danmSend`, `startUpload` are also emitted.

### API Request Layer

Both apps share an identical request architecture (`src/utils/Request.js`):

- **Axios instance**: `baseURL: "/api"`, `withCredentials: true`, `timeout: 10s`
- **Request interceptor**: Shows `ElLoading` overlay if `config.showLoading` is true
- **Response interceptor**: `code == 200` -> success; `code == 901` -> login timeout, triggers `loginStore.setLogin(true)`; other codes -> error with `errorCallback`
- **Request function**: Always uses **POST** method. Parameters are packed into a `FormData` object (even for non-file data). Auth token is read from cookie via `VueCookies.get('token')` and sent as a **`token` header**. Also sends `X-Requested-With: XMLHttpRequest`.
- Supports `dataType: 'json'` (switches to JSON content type), `responseType` (arraybuffer/blob for file downloads), and `uploadProgressCallback` for upload progress.
- Global properties registered in `main.js`: `proxy.request`, `proxy.Api`, `proxy.Message`, `proxy.Confirm`, `proxy.Utils`, `proxy.Verify`, `proxy.VueCookies`.

#### API Endpoints (User App)

The `src/utils/Api.js` file defines ~50 endpoint constants. Key groups:

| Category | Endpoints |
|---|---|
| Account | `checkCode`, `login`, `logout`, `register`, `autoLogin`, `getUserCountInfo` |
| File/Upload | `preUploadVideo`, `uploadVideo` (chunked, 512KB), `delUploadVideo`, `uploadImage`, `getVideoResource`, `sourcePath` |
| Video | `loadRecommendVideo`, `loadVideo`, `loadVideoPList`, `getVideoInfo`, `search`, `getSearchKeywordTop`, `getVideoRecommend`, `hotVideoList` |
| User actions | `userAction` (actionType: 0=video-like, 1=undefined toggle, 2=not handled, 3=video-collect, 4=video-coin; see user_action table for full details), `saveVideoInteraction` |
| Comments | `loadComment`, `postComment`, `userDelComment`, `userTopComment`, `userCancelTopComment`, `loadReply`, `likeComment` |
| Danmaku | `loadDanmu`, `postDanmu` |
| User center | `postVideo`, `getVideoByVideoId`, `loadUcenterVideoList`, `getUcenterVideoCountInfo`, `ucLoadAllVideo`, `ucLoadComment`, `ucDelComment`, `ucLoadDanmu`, `ucDelDanmu`, `ucGetActualTimeStatisticsInfo`, `getWeekStatisticsInfo`, `ucDeleteVideo` |
| User home | `uHomeUpdateUserInfo`, `uHomeLoadVideoList`, `uHomeGetUserInfo`, `uHomeFocus`, `uHomeCancelFocus`, `uHomeFocusList`, `uHomeFansList`, `uHomeLoadUserCollection`, `saveTheme` |
| Video series | `uHomeSeriesLoadVideoSeries`, `uHomeSeriesLoadAllVideo`, `uHomeSeriesSaveVideoSeries`, `uHomeSeriesChangeVideoSeriesSort`, `uHomeSeriesGetVideoSeriesDetail`, `uHomeSeriesDelVideoSeries`, `uHomeSeriesSaveSeriesVideo`, `uHomeSeriesDelSeriesVideo`, `uHomeSeriesLoadVideoSeriesWithVideo` |
| System | `loadCategory`, `getSysSetting` |

#### API Endpoints (Admin App)

| Category | Endpoints |
|---|---|
| Auth | `checkCode`, `login`, `uploadImage`, `sourcePath` |
| Category | `loadCategory`, `saveCategory`, `deleteCategory`, `changeCategorySort` |
| Video | `loadVideo`, `loadVideoPList`, `getVideoResource`, `auditVideo`, `deleteVideo` |
| Settings | `getSetting`, `saveSetting` |
| Dashboard | `getActualTimaStatisticsInfo`, `getWeekStatisticsInfo` |
| Review/Audit | `reviewList`, `reviewVideo`, `reviewDetail`, `auditStats` |
| Video management | `videoManagementList`, `videoManagementRecommend`, `videoManagementDelete`, `videoManagementStats` |
| Interactions | `loadComment`, `delComment`, `loadDanmu`, `delDanmu` |
| Users | `loadUser`, `changeStatus` |

#### Chunked Upload Flow

The `VideoUploader.vue` implements chunked/resumable upload:

1. `preUploadVideo` -> gets `uploadId` from server
2. File split into 512KB chunks (`chunkSize = 1024 * 512`)
3. Each chunk uploaded via `uploadVideo` with `chunkIndex` + `uploadId`
4. Supports pause/resume (`pauseUpload` / `restartUpload`)
5. Supports drag-to-reorder P-episodes via `v-draggable`
6. Max concurrent uploads: 1 (`maxUploading`)

### Video Player

The platform uses a **multi-layered video playback approach**:

#### Primary player: ArtPlayer + HLS.js (`components/Player.vue`)

- **ArtPlayer** (`^5.3.0`) configured with `type: 'm3u8'` using `customType.m3u8` with **HLS.js** (`^1.5.13`). Checks `Hls.isSupported()`, falls back to native `video.canPlayType('application/vnd.apple.mpegurl')` for Safari.
- Features enabled: autoplay, fullscreen, fullscreenWeb, PiP, playback rate, flip, aspect ratio, screenshot, auto-playback (resume), wide-screen toggle.
- Right-click context menu disabled (`Artplayer.CONTEXTMENU = false`). Theme color `#23ade5`.
- **Danmuku** via **artplayer-plugin-danmuku** (`^5.1.4`): Danmaku list loaded async via `loadDanmu` API (returns array of `{content, color, time}`). `beforeEmit` hook posts new danmaku via `postDanmu` API, then reloads the list.
- Shows online viewer count and loaded danmaku count.
- Listens to event bus `changeF` event to switch video file (episode), emits `playEnd` on video completion.

#### Secondary danmaku overlay: `DanmuPlayer.vue`

A custom CSS-based scrolling-comment overlay (not using ArtPlayer's plugin). Loads danmaku via API, randomly displays items with an 8-second `@keyframes danmuMove` CSS animation. Used alongside the ArtPlayer in `VideoDetail.vue`.

#### Admin preview player (VideoAudit.vue)

Uses **ArtPlayer + HLS.js** in a dialog for video review, streaming from `/api/file/videoResourcePost/${playFileId}/index.m3u8`. Destroyed/recreated on dialog open/close.

### Reusable Components

#### User App Components (`src/components/`)

| Component | Purpose |
|---|---|
| `Player.vue` | Main video player wrapping ArtPlayer + HLS.js + danmuku plugin |
| `DanmuPlayer.vue` | Custom CSS-based scrolling danmaku overlay |
| `VideoItem.vue` | Video card for grids (cover, title, play count, author, date); supports grid/horizontal layouts; globally registered |
| `Cover.vue` | Image display with lazy loading, placeholder, error fallback; handles both File objects and URL strings; globally registered |
| `Avatar.vue` | Circular avatar wrapper around Cover; falls back to letter-avatar; globally registered |
| `Dialog.vue` | Wrapper around `el-dialog` with custom header/footer, draggable, max-height; globally registered |
| `ActionBar.vue` | Like/Coin/Collect/Share action bar for videos |
| `CommentList.vue` | Comment list with hot/latest sort tabs, pagination, integrates CommentInput + CommentItem |
| `CommentItem.vue` | Single comment with avatar, content, nested replies, delete, expand/collapse |
| `CommentInput.vue` | Textarea for posting comments/replies |
| `ImageCoverSelect.vue` | Cover image upload selector for the post-video form |
| `ImageCoverCut.vue` | Image cropper dialog using vue-cropper with fixed aspect ratio |

#### Admin App Components (`src/components/`)

| Component | Purpose |
|---|---|
| `Table.vue` | Generic data table wrapper around `el-table` + `el-pagination`; props: columns config, dataSource, fetch function; exposes refresh, resetPagination, setCurrentRow, clearSelection; globally registered |
| `Cover.vue` | Same image display pattern as web app |
| `Avatar.vue` | Same avatar pattern |
| `Dialog.vue` | Same dialog wrapper |

---

## Key Design Decisions

### Multi-Module Maven Architecture

Shared `easylive-common` module with entities, mappers, services, and utils is used by both `easylive-web` (user-facing) and `easylive-admin` (admin panel). Two separately-deployed Spring Boot applications share the same database and common codebase, avoiding code duplication while maintaining deployment isolation.

### Staging Table Pattern

Videos go through `tb_video_info_post` (staging) -> audit -> `tb_video_info` (published). This separates unpublished content from published content at the database level. Approved videos are **copied** (not moved) — both records exist independently. This allows the post table to retain audit history while the published table serves user-facing queries.

### DTO/VO/Query Pattern

- **PO (Persistent Objects)** in `entity/po/` — map 1:1 to database tables
- **Query Objects** in `entity/query/` — parameter objects for MyBatis queries, extend `BaseQuery` with pagination support (`SimplePage`)
- **DTO** in `entity/dto/` — `TokenUserInfoDto` (auth token payload), `SysSettingDto` (system config), `UploadingFileDto` (upload state), `AiAuditResult`
- **VO (View Objects)** in `entity/vo/` — `ResponseVO<T>` (unified API response), `PaginationResultVO<T>`, `VideoInfoResultVO`, `VideoStatusCountInfoVO`

### No Spring Security

Custom token + interceptor approach instead of Spring Security framework. Simple and lightweight, but no RBAC — only two flat roles (regular user, admin). Token is a UUID stored in Redis, not a JWT.

### MyBatis with XML Mappers

Full SQL control via XML mapper files. No JPA/Hibernate. `map-underscore-to-camel-case: true` for automatic column-to-property mapping.

### Denormalized Counters

`tb_video_info` stores `play_count`, `like_count`, `coin_count`, `collect_count`, `comment_count`, `danku_count` directly as columns. These are updated on each user action rather than computing from the `user_action` table. This trades write amplification for read performance.

### Redis List-Based Queue (No MQ)

Video transcoding and file deletion tasks are queued in Redis lists (`lpush` / `rpop` pattern) instead of using RabbitMQ/Kafka. A `@PostConstruct` thread pool (`Executors.newFixedThreadPool(2)` capacity, but only 1 thread actively polls — the second `@PostConstruct` method is commented out) polls the queue continuously. This keeps the stack simple — no additional infrastructure required.

### Async Processing via @PostConstruct

`ExecuteQueueTask` starts a thread pool on application startup (`Executors.newFixedThreadPool(2)` capacity, but only 1 thread actively polls the queue — a second `@PostConstruct` method `consumFileQueue()` is commented out). It continuously polls the Redis transfer queue. This decouples video transcoding from the request thread, allowing uploads to return immediately while transcoding happens in the background.

### Soft Deletes

Comments use `status = -1` for admin deletions (preserving the record). Danmaku uses `is_show = 0`. User accounts support three statuses: 0:disabled, 1:active, -1:deactivated. Videos are hard-deleted with cascading deletes from related tables.

### Path Safety

`StringTools.pathIsOk()` validates file paths to prevent directory traversal attacks in the admin module's file-serving endpoints. Admin `FileController` normalizes m3u8 content (strips query params, extracts filenames) for security when serving pre-publish video previews.

### .env Loading via Custom Spring Processor

`DotEnvPostProcessor` in `easylive-common` is a custom Spring environment processor that loads the `.env` file from the project root into the Spring environment. This allows `application.yml` to reference environment variables via Spring placeholders (e.g., `${DB_PASSWORD:}`, `${VOLCENGINE_API_KEY:}`) without requiring OS-level environment variable configuration.

### Frontend: All-POST with FormData

The frontend always uses POST method for all API requests (even reads like fetching video lists). Parameters are packed into a `FormData` object rather than JSON. This design choice simplifies the request layer (one code path for both file uploads and regular requests) but means the API is not RESTful.

### Frontend: Client-Side MD5 Password Hashing

Passwords are MD5-hashed on the client side via `js-md5` before being sent to the server. This is a minimal security measure — it prevents plaintext passwords from being transmitted, but MD5 is cryptographically weak. The server applies an **additional MD5 hash** on the password during registration (via `StringTools.encodeByMd5()`), resulting in a double-MD5 stored in the database. During login, the server compares the received (client-hashed) password directly against the stored value without additional hashing.

### Frontend: No Navigation Guards

Authentication is handled reactively rather than proactively. There are no Vue Router `beforeEach` guards. Instead, when the backend returns code 901 (session expired), the response interceptor sets `loginStore.setLogin(true)`, which shows a login dialog overlay. This means users can navigate to any route without being redirected to a login page — they simply see the login dialog when an API call fails.

### Frontend: Bilibili-Style UI

The visual design deliberately mimics Bilibili (B站): blue `#00AEEC` primary color, pink `#fb7299` accent, card/grid layouts, header with avatar hover panel showing follow/fans counts, category navigation bar with sub-category popovers, and the upload/投稿 flow. CSS custom properties defined in `assets/scss/base.scss`: `--text: #18191C`, `--text2: #61666D`, `--text3: #9499a0`, `--blue: #00AEEC`, `--blue2: #007fff`, `--blue3: #00a1d6`. Font stack: PingFang SC, HarmonyOS, Microsoft YaHei.
