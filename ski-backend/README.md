# 黑龙江滑雪场管理系统

本项目是一套面向本科毕业设计的滑雪场综合管理系统后端。系统以黑龙江滑雪旅游场景为背景，在原有票务功能上增加雪场信息、教练、基础设施、维护、安全巡检和游客反馈管理，使业务范围从“售票”扩展为“雪场日常运营管理”。

项目保持常见的 Spring Boot 单体结构，便于学习、部署、答辩演示和后期开发前端，不使用微服务、消息队列等不必要的复杂技术。

## 技术栈

- Java 17
- Spring Boot 4
- Spring MVC、Spring Security、JWT
- MyBatis
- MySQL 8
- Maven、Lombok
- 阿里云 OSS（可选，用于图片上传）

## 功能模块

### 游客端

- 注册、登录和退出
- 浏览滑雪场、雪道、天气雪况与公告
- 浏览雪票、雪具、课程、教练和服务设施
- 购买雪票、租赁雪具、报名课程
- 提交评价或意见反馈并查看回复

### 管理端

- 员工登录与账号管理
- 滑雪场、雪道、雪票、雪具和课程管理
- 教练与基础设施管理
- 订单查询和状态处理
- 天气雪况与公告发布
- 设施维护记录和雪道安全巡检
- 游客反馈处理与回复

## 数据库设计

数据库共 15 张表：

| 序号 | 表名 | 用途 |
|---:|---|---|
| 1 | `ski_resort` | 黑龙江滑雪场基本信息 |
| 2 | `user` | 游客账号 |
| 3 | `employee` | 管理员和工作人员 |
| 4 | `coach` | 教练档案 |
| 5 | `ski_run` | 雪道信息 |
| 6 | `ticket` | 雪票产品 |
| 7 | `equipment` | 雪具库存和租金 |
| 8 | `courses` | 滑雪课程 |
| 9 | `announcements` | 公告信息 |
| 10 | `weather` | 天气和雪况 |
| 11 | `orders` | 雪票、雪具和课程订单 |
| 12 | `facility` | 缆车、造雪机和救援设施 |
| 13 | `maintenance_record` | 设施维护记录 |
| 14 | `safety_inspection` | 雪道安全巡检记录 |
| 15 | `feedback` | 游客评价和意见反馈 |

完整建库脚本及少量答辩演示数据位于：

```text
sql/heilongjiang_ski_management.sql
```

## 本地运行

1. 安装 JDK 17 和 MySQL 8。
2. 在 MySQL 中执行 `sql/heilongjiang_ski_management.sql`。
3. 根据本机环境修改数据库密码，或设置环境变量 `DB_PASSWORD`。
4. 运行：

```powershell
.\mvnw.cmd spring-boot:run
```

服务默认地址为 `http://localhost:8081`。

演示账号：

- 管理端：`admin / 123456`
- 游客端：`student / 123456`

## 常用接口

公开查询接口：

- `GET /user/resort/list`
- `GET /user/ski-run/list`
- `GET /user/weather/list`
- `GET /user/coach/list`
- `GET /user/facility/list`
- `GET /user/ticket/list`
- `GET /user/course/list`

需要游客令牌：

- `POST /user/ticket/buy/{ticketId}`
- `POST /user/equipment/rent/{equipmentId}`
- `POST /user/course/join/{courseId}`
- `GET /user/user/profile`
- `GET /user/orders/my`
- `GET /user/orders/{id}`
- `PUT /user/orders/{id}/pay?paymentMethod=微信`
- `PUT /user/orders/{id}/cancel?cancelReason=行程变化`
- `PUT /user/orders/{id}/return`
- `POST /user/feedback`
- `GET /user/feedback/my`

管理接口统一使用 `/admin/**`，除 `/admin/employee/login` 外均需要管理员令牌。登录成功后把 JWT 放入名为 `token` 的请求头。

管理端首页统计接口：`GET /admin/dashboard/statistics`，返回核心数量指标、订单类型分布和最近 7 天营业额。

## 配置说明

数据库、JWT 和 OSS 密钥均支持通过环境变量设置：

- `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`
- `SKI_JWT_SECRET`
- `ALIYUN_OSS_ACCESS_KEY_ID`
- `ALIYUN_OSS_ACCESS_KEY_SECRET`
- `ALIYUN_OSS_BUCKET_NAME`

OSS 只用于图片上传。不演示上传功能时可以不配置 OSS 密钥。

## 毕业设计建议

论文可以围绕四个业务部分展开：

1. 雪场基础资源管理：雪场、雪道、设施、雪具。
2. 游客服务管理：雪票、课程、教练、订单和反馈。
3. 运营信息管理：天气雪况和公告。
4. 安全保障管理：设施维护和雪道巡检。

后续前端只需按照现有 REST 接口实现用户端和管理端页面，无需改变后端整体架构。
