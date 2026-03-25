# 图片资源聚合项目

## 项目介绍

图片资源聚合项目是一个前后端分离的Web应用，用于管理和分享图片资源。系统支持图片上传、管理、查看等功能，适用于个人或团队的图片资源管理需求。

## 技术栈

### 后端技术栈
- Java 17
- Spring Boot 2.7.6
- MyBatis-Plus 3.5.9
- Redis
- 腾讯云 COS
- Knife4j API文档

### 前端技术栈
- Vue 3
- TypeScript
- Ant Design Vue 4.2.6
- Pinia
- Vue Router
- Vite

## 项目结构

```
p/
├── picture-backend/         # 后端项目
│   ├── src/main/java/com/yang/picturebackend/  # 后端源代码
│   │   ├── controller/      # 控制器
│   │   ├── service/         # 服务层
│   │   ├── mapper/          # 数据访问层
│   │   ├── model/           # 数据模型
│   │   ├── config/          # 配置类
│   │   ├── common/          # 公共类
│   │   ├── exception/       # 异常处理
│   │   ├── manager/         # 管理器
│   │   ├── annotation/      # 注解
│   │   └── aop/             # 切面
│   ├── src/main/resources/  # 资源文件
│   └── pom.xml              # Maven配置文件
└── picture-frontend/        # 前端项目
    ├── src/                 # 前端源代码
    │   ├── api/             # API接口
    │   ├── components/      # 组件
    │   ├── pages/           # 页面
    │   ├── router/          # 路由
    │   ├── stores/          # 状态管理
    │   └── utils/           # 工具类
    ├── public/              # 静态资源
    └── package.json         # npm配置文件
```

## 功能特性

- **核心架构**：基于自研的 Spring Boot 项目模板 + MyBatis Plus + MyBatis X 插件构建核心业务，实现用户、图片、空间等实体表的 CRUD 操作
- **依赖整合**：使用 Spring Initializr 初始化项目，实现 MyBatis Plus、Hutool、Knife4j 接口文档、Lombok 等主流依赖整合
- **数据操作**：引入 MyBatis Plus 实现 Mapper 自动扫描，并基于其 BaseMapper + 自定义分页拦截器快速实现 CRUD 操作，减少了大量样板代码
- **异常管理**：统一异常管理，封装了自定义异常类及统一错误码枚举类，并基于 @RestControllerAdvice 注解开发全局异常处理器，提升了系统的稳定性
- **响应封装**：统一响应封装，自定义响应封装类和返回成功/失败响应的工具类，实现了标准化的接口响应数据格式，便于前后端交互
- **跨域配置**：全局跨域配置，基于 WebMvcConfigurer 实现了全局 CORS 配置类，配置可信源，解决了前后端跨域请求问题
- **精度处理**：精度丢失解决，自定义 Jackson 配置类，通过 ObjectMapper 配置 Long 类型序列化为字符串，解决前端因 JS 精度限制导致的长整型数据精度丢失问题
- **API文档**：使用 Knife4j 自动生成接口文档，并基于 ApiOperation 注解编写接口注释，减少了人工维护文档的工作量
- **用户安全**：用户注册使用 MD5 + 盐值加密策略对用户密码进行加密存储，防止明文密码泄露风险
- **登录态管理**：用户登录通过 Redis 整合 Spring Session 分布式保存用户登录态，实现了跨服务器的用户识别
- **权限管理**：基于自定义权限校验注解和 Spring AOP 环绕切面，实现了统一的权限校验，区分用户和管理员
- **图片存管**：基于 COS 对象存储 SDK 封装了通用文件上传下载服务，并基于数据万象服务自动解析图片信息（包括宽高、格式、大小等）
- **图片审核**：设计分级审核策略，管理员上传图片自动通过审核，并自动记录审核时间与审核人 id，确保可审计追溯
- **URL传图**：使用 Hutool 请求 URL 来下载图片，并在下载前通过 Head 请求校验 URL，有效降低带宽消耗与数据异常风险
- **设计模式**：运用模板方法设计模式统一封装本地图片和 URL 图片上传的流程，如校验、下载、上传和资源释放，复用代码并提高可维护性
- **查询优化**：为提高主页热门图片的查询性能，采用 Redis + Caffeine 构建多级缓存，接口响应耗时降低 400%，并通过随机过期时间降低缓存雪崩风险
- **加载优化**：利用数据万象，在上传前对大文件进行 Webp 转码压缩和缩略图生成，大幅提高页面加载速度并节约成本

## 系统截图

### 首页

![image-20260325160640437](README.assets/image-20260325160640437.png)

### 图片上传页面

![image-20260325160654558](README.assets/image-20260325160654558.png)

### 图片批量抓取页面

![image-20260325160703128](README.assets/image-20260325160703128.png)

### 图片管理页面

![image-20260325160709046](README.assets/image-20260325160709046.png)

### 用户登录页面

![image-20260325160716381](README.assets/image-20260325160716381.png)

## 快速开始

### 后端部署

1. 克隆项目到本地

2. 配置数据库
   - 创建数据库
   - 执行 `picture-backend/sql/create_table.sql` 创建表结构
- 在 `picture-backend/src/main/resources/application.yml` 中配置MySQL相关参数
  
3. 配置腾讯云COS
   
- 在 `picture-backend/src/main/resources/application.yml` 中配置COS相关参数
  
4. 配置Redis
   
- 在 `picture-backend/src/main/resources/application.yml` 中配置Redis相关参数
  
5. 构建和运行
   ```bash
   cd picture-backend
   mvn clean package
   java -jar target/picture-backend-0.0.1-SNAPSHOT.jar
   ```

### 前端部署

1. 安装依赖
   ```bash
   cd picture-frontend
   npm install
   ```

2. 配置API地址
   
- 修改 `picture-frontend/src/request.ts` 中的API基础地址
  
3. 开发环境运行
   ```bash
   npm run dev
   ```

4. 生产环境构建
   ```bash
   npm run build
   ```

## API文档

后端提供了完整的API文档，可通过以下地址访问：

```
http://localhost:8080/api/doc.html
```

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request。

## 联系方式

如有问题，请联系项目维护者。