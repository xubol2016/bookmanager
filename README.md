# 📚 BookManager - 图书管理系统

一个功能完善的图书馆管理系统，采用 Spring Boot 3 + Vue 3 全栈技术开发，提供图书管理、借阅管理、用户管理等核心功能。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-blue.svg)](https://vuejs.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-Latest-409EFF.svg)](https://element-plus.org/)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.5-red.svg)](https://baomidou.com/)

## ✨ 功能特性

### 📖 图书管理
- 图书的增删改查，支持批量操作
- 图书分类管理（支持多级分类）
- 图书封面上传
- 高级搜索（书名、作者、ISBN、出版社、分类、年份等）
- 搜索历史和自动补全

### 📅 借阅管理
- 借书申请与审批
- 还书处理
- 续借功能
- 超期自动监控（定时任务）
- 借阅历史记录
- 借阅统计分析

### 👥 用户管理
- 用户注册与登录
- JWT 身份认证
- 基于角色的访问控制（普通用户/管理员）
- 个人信息管理

### 📊 数据统计
- 系统概览仪表板
- 图书借阅趋势分析（ECharts）
- 图书分类分布统计
- 热门图书排行
- 超期图书监控

### 🔔 系统公告
- 公告发布与管理
- 置顶公告
- 定时发布功能

### 🎨 界面特色
- 响应式设计，支持移动端
- 精美的 UI 界面（Element Plus）
- 深色/浅色主题
- 标签页导航
- 全屏模式

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 3.2.1 (Java 17)
- **ORM**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0
- **安全**: JWT (JSON Web Token)
- **构建工具**: Maven 3.9.x

### 前端技术
- **框架**: Vue 3 (Composition API)
- **UI 库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **图表**: ECharts 5
- **构建工具**: Vite 5

## 📁 项目结构

```
bookmanager/
├── src/main/java/top/xubol/bookmanager/    # 后端源码
│   ├── controller/                          # 控制器层
│   ├── service/                             # 服务层
│   ├── mapper/                              # 数据访问层
│   ├── entity/                              # 实体类
│   ├── dto/                                 # 数据传输对象
│   ├── vo/                                  # 视图对象
│   ├── config/                              # 配置类
│   ├── common/                              # 公共类
│   ├── util/                                # 工具类
│   └── task/                                # 定时任务
├── src/main/resources/
│   ├── mapper/                              # MyBatis XML 映射文件
│   └── application.yml                      # 应用配置文件
├── coursefront/                             # 前端项目
│   ├── src/
│   │   ├── api/                             # API 接口
│   │   ├── components/                      # 公共组件
│   │   ├── layout/                          # 布局组件
│   │   ├── router/                          # 路由配置
│   │   ├── stores/                          # Pinia 状态管理
│   │   ├── utils/                           # 工具函数
│   │   └── views/                           # 页面组件
│   └── vite.config.js                       # Vite 配置
├── coursemanager.sql                        # 数据库脚本
├── CLAUDE.md                                # 项目文档
└── pom.xml                                  # Maven 配置
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.9+

### 后端部署

1. **创建数据库**
```sql
CREATE DATABASE coursemanager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **导入数据库脚本**
```bash
mysql -u root -p coursemanager < coursemanager.sql
```

3. **配置数据库连接**

编辑 `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/coursemanager
    username: root
    password: your_password
```

4. **运行后端**
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端部署

1. **安装依赖**
```bash
cd coursefront
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

前端应用将在 `http://localhost:3000` 启动

3. **生产构建**
```bash
npm run build
```

### 默认账号

- **管理员**: 
  - 用户名: `admin`
  - 密码: `admin123`

- **普通用户**: 
  - 用户名: `user`
  - 密码: `user123`

## 📸 功能截图

### 门户首页
- 轮播图展示
- 快速搜索
- 图书推荐
- 热门排行

### 工作台
- 数据统计卡片
- 借阅趋势图表
- 分类分布图
- 快捷入口

### 图书管理
- 网格/列表视图切换
- 高级搜索
- 筛选标签
- 图书详情

### 借阅管理
- 借阅申请
- 审批流程
- 超期监控
- 统计分析

## 🔧 核心功能说明

### JWT 认证机制
系统采用 JWT (JSON Web Token) 实现无状态认证：
- Token 有效期：24 小时
- 基于角色的权限控制
- 使用拦截器统一验证

### 搜索功能
- **快速搜索**: 关键词匹配书名、作者、ISBN
- **高级搜索**: 支持多条件组合查询
- **搜索建议**: 实时显示匹配结果
- **搜索历史**: 本地存储最近 10 条搜索记录

### 定时任务
- 每日凌晨自动检测超期图书
- 自动更新借阅状态
- 定时发布公告

## 📝 API 文档

### 认证相关
```
POST   /api/users/login      用户登录
POST   /api/users/register   用户注册
GET    /api/users/profile    获取个人信息
```

### 图书管理
```
GET    /api/books            分页查询图书
GET    /api/books/{id}       获取图书详情
POST   /api/admin/books      添加图书（管理员）
PUT    /api/admin/books      更新图书（管理员）
DELETE /api/admin/books/{id} 删除图书（管理员）
```

### 借阅管理
```
POST   /api/borrow/apply     申请借书
PUT    /api/borrow/approve   审批借书（管理员）
PUT    /api/borrow/return    归还图书
GET    /api/borrow/my        我的借阅记录
```

更多 API 详见 Controller 层代码注释

## 🤝 贡献指南

欢迎提交 Issue 或 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 👨‍💻 作者

**xubol2016**

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis Plus](https://baomidou.com/)
- [ECharts](https://echarts.apache.org/)

---

⭐ 如果这个项目对你有帮助，请给它一个 Star！
