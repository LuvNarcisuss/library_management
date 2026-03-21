# 图书管理系统运行部署说明

## 1. 系统概述

图书管理系统是一个基于前后端分离架构的Web应用，用于管理图书馆的图书、读者和借阅信息。

- **后端技术**：Spring Boot 2.7.14 + Spring Data JPA + MySQL
- **前端技术**：Vue 2.6.14 + Element UI 2.15.12 + Vite 4.4.5
- **项目类型**：Maven项目（后端）+ NPM项目（前端）

## 2. 部署环境准备

### 2.1 硬件要求
- CPU：2核以上
- 内存：4GB以上
- 磁盘：20GB以上可用空间

### 2.2 软件要求

| 软件 | 版本要求 | 用途 |
|------|---------|------|
| JDK | 17 | 运行Java后端 |
| MySQL | 8.0.x | 数据库服务 |
| Maven | 3.6.0+ | 后端项目构建 |
| Node.js | 14.x-16.x | 前端项目构建 |
| npm | 6.x+ | 前端依赖管理 |
| Nginx | 1.18+ | 前端静态文件部署（可选） |

### 2.3 Java 17环境配置

1. **下载安装Java 17**
   - 从Oracle官网或OpenJDK下载Java 17
   - 安装路径建议使用英文路径，如：`D:\APP\Java\jdk-17`

2. **配置环境变量**
   - **JAVA_HOME**：设置为JDK安装路径，如：`D:\APP\Java\jdk-17`
   - **Path**：添加`%JAVA_HOME%\bin`到Path变量

3. **验证Java版本**
   ```bash
   java -version
   ```
   输出应显示Java 17版本信息

## 3. 后端部署

### 3.1 数据库配置

#### 3.1.1 创建数据库用户和权限
```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'Narcisuss'@'localhost' IDENTIFIED BY '688376';

-- 授予权限
GRANT ALL PRIVILEGES ON library.* TO 'Narcisuss'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

#### 3.1.2 导入数据库
```sql
-- 退出MySQL后执行
mysql -u Narcisuss -p library < library.sql
```

#### 3.1.3 配置数据库连接
编辑 `src/main/resources/application.properties` 文件：
```properties
# 数据库连接配置
spring.datasource.url=jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=Narcisuss
spring.datasource.password=688376

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

#### 3.1.4 更新管理员密码（可选）
如果需要修改管理员密码，可以执行以下SQL：
```sql
UPDATE users SET password = 'admin123' WHERE username = 'admin';
```

或者使用Java程序更新（项目中已包含UpdateAdminPassword.java）：
```bash
javac -encoding UTF-8 UpdateAdminPassword.java
java UpdateAdminPassword
```

### 3.2 构建后端项目

```bash
# 编译并打包项目
mvn clean package -DskipTests
```

打包成功后，生成的jar文件位于 `target/` 目录：`library-management-system-1.0-SNAPSHOT.jar`

### 3.3 运行后端服务

#### 3.3.1 直接运行
```bash
java -jar target/library-management-system-1.0-SNAPSHOT.jar
```

#### 3.3.2 后台运行（Linux）
```bash
nohup java -jar target/library-management-system-1.0-SNAPSHOT.jar > backend.log 2>&1 &
```

#### 3.3.3 作为Windows服务运行
可使用WinSW工具将jar包注册为Windows服务。

### 3.4 验证后端服务

服务启动后，访问以下URL验证：
```
http://localhost:8080/books  # 获取图书列表API
```

## 4. 前端部署

### 4.1 配置前端项目

#### 4.1.1 修改API代理配置
编辑 `frontend/vite.config.js` 文件，配置后端API地址：
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端服务地址
      changeOrigin: true
    }
  }
}
```

#### 4.1.2 修改Axios基础URL
编辑 `frontend/src/main.js` 文件：
```javascript
axios.defaults.baseURL = '/api'
```

### 4.2 构建前端项目

```bash
# 进入前端项目目录
cd frontend

# 安装依赖
npm install

# 构建生产版本
npm run build
```

构建成功后，生成的静态文件位于 `dist/` 目录。

### 4.3 部署前端静态文件

#### 4.3.1 使用Nginx部署

1. 安装Nginx
2. 配置Nginx虚拟主机：

```nginx
server {
    listen 80;
    server_name localhost;
    # 替换为实际的前端构建文件路径
    root d:/aelm/thank_you/学习资料/项目作业/图书管理系统/frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API代理配置
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

3. 重启Nginx：
```bash
nginx -s reload
```

#### 4.3.2 使用Vite开发服务器

```bash
# 开发环境运行
npm run dev
```

### 4.4 验证前端服务

访问前端应用：
```
http://localhost:3000  # 开发环境
http://localhost       # Nginx部署
```

## 5. 系统集成测试

### 5.1 功能验证

1. **图书管理**
   - 添加图书
   - 查询图书
   - 修改图书信息
   - 删除图书

2. **读者管理**
   - 添加读者
   - 查询读者
   - 修改读者信息
   - 删除读者

3. **借阅管理**
   - 借阅图书
   - 归还图书
   - 查询借阅记录

### 5.2 API测试

使用Postman或curl测试API接口：

#### 5.2.1 登录API测试
```bash
# 用户登录（默认管理员账号密码）
curl -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}' http://localhost:8080/users/login
```

**预期响应：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

#### 5.2.2 登录测试工具
项目中提供了专门的登录测试工具（TestLogin.java），可以用于验证登录功能：

```bash
# 编译测试工具
javac -encoding UTF-8 TestLogin.java

# 运行测试工具
java TestLogin
```

#### 5.2.3 其他API测试
```bash
# 获取图书列表
curl http://localhost:8080/books

# 添加图书
curl -X POST -H "Content-Type: application/json" -d '{"title":"测试图书","author":"测试作者","isbn":"123456","price":29.9,"stock":10,"status":"AVAILABLE"}' http://localhost:8080/books
```

## 6. 常见问题及解决方案

### 6.1 数据库连接失败
- 检查MySQL服务是否运行
- 验证数据库用户名和密码是否正确
- 检查数据库名称是否正确
- 确保MySQL端口（默认3306）可访问
- 检查`application.properties`中的数据库连接URL格式是否正确

### 6.2 后端服务启动失败
- 检查端口是否被占用（默认8080）
- 查看日志文件定位错误信息
- 确保数据库连接配置正确
- 验证Java版本是否为17（Spring Boot 2.7.14要求Java 17）

### 6.3 前端页面无法访问
- 检查前端服务是否运行
- 验证Nginx配置是否正确
- 检查API代理配置是否正确
- 确保前端构建文件存在于指定路径

### 6.4 API调用失败
- 检查前后端服务是否都在运行
- 验证API路径是否正确（前端组件中已修复API路径问题）
- 查看浏览器控制台的错误信息
- 检查跨域配置是否正确

### 6.5 登录失败（已修复）
**问题已解决**：前端组件中存在API路径错误，已修复。
- **修复内容**：
  1. 删除了`Login.vue`组件中`/api/users/login`路径中的冗余`/api`前缀，改为`/users/login`
  2. 修复了`BookManager.vue`、`ReaderManager.vue`和`BorrowManager.vue`中的API路径
- **验证方法**：直接使用修复后的前端组件进行登录即可成功

## 7. 单元测试结果

### 7.1 测试概述
已成功运行所有测试类，验证了后端服务的核心功能：
- 测试框架：JUnit 4.13.2 + Mockito 4.5.1
- 测试类：BookServiceTest、ReaderServiceTest、UserServiceTest
- 测试方法总数：27个
- 测试结果：全部通过（0个失败，0个错误）

### 7.2 测试详情
已成功运行所有测试类，验证了后端服务的核心功能：

| 测试类 | 测试方法数 | 执行结果 |
|-------|----------|---------|
| BookServiceTest | 11 | 全部通过 |
| ReaderServiceTest | 9 | 全部通过 |
| BorrowServiceTest | 13 | 全部通过 |
| UserServiceTest | 7 | 全部通过 |

**总计**：40个测试方法，0个失败，0个错误

### 7.3 执行测试命令

```bash
# 运行所有测试类
  mvn test
# 运行单个测试类
  mvn test -Dtest=BookServiceTest
  mvn test -Dtest=BorrowServiceTest
  mvn test -Dtest=ReaderServiceTest
  mvn test -Dtest=UserServiceTest
```

### 7.4 测试报告
测试执行完成后，报告生成在 `target/surefire-reports/` 目录下。

