# Gradle Jib 示例项目

这是一个使用 Gradle 和 Jib 将 Spring Boot 应用打包成 Docker 镜像的示例项目。

## 项目特性

* **Spring Boot 3.2.0**: 现代化的 Spring Boot 框架
* **Spring Data JPA**: 数据访问层
* **H2 Database**: 内存数据库
* **JUnit 5**: 单元测试框架
* **Mockito**: Mock测试框架
* **Gradle**: 项目构建工具
* **Jib**: Google 的容器镜像构建工具

## 前置要求

* JDK 17 或更高版本
* Gradle 8.5 或更高版本（项目包含 Gradle Wrapper）
* Docker（可选，如果要将镜像推送到本地Docker daemon）

## 使用方法

### 1. 构建项目

```bash
./gradlew build
```

Windows 系统使用：

```bash
gradlew.bat build
```

### 2. 使用 Jib 构建 Docker 镜像

#### 方式一：构建并加载到本地 Docker（推荐）

使用默认配置：

```bash
./gradlew jibDockerBuild
```

Windows 系统使用：

```bash
gradlew.bat jibDockerBuild
```

使用自定义镜像配置：

```bash
# 指定基础镜像和目标镜像
./gradlew jibDockerBuild \
  -Pjib.from.image=eclipse-temurin:17-jre-alpine \
  -Pjib.to.image=my-custom-image:1.0.0

# 或者只覆盖目标镜像
./gradlew jibDockerBuild -Pjib.to.image=my-registry.com/my-app:v1.0.0
```

这会在本地构建镜像并加载到 Docker daemon 中。

#### 方式二：构建 Docker tar 文件

使用默认配置：

```bash
./gradlew jibBuildTar
```

使用自定义镜像配置：

```bash
./gradlew jibBuildTar \
  -Pjib.from.image=eclipse-temurin:17-jre-alpine \
  -Pjib.to.image=my-app:latest
```

这会生成一个 tar 文件：`build/jib-image.tar`，可以使用以下命令加载：

```bash
docker load -i build/jib-image.tar
```

#### 方式三：直接推送到远程仓库

使用命令行参数指定远程仓库地址：

```bash
# 需要先登录到镜像仓库
docker login your-registry.com

# 构建并推送到远程仓库
./gradlew jib \
  -Pjib.from.image=eclipse-temurin:17-jre-alpine \
  -Pjib.to.image=your-registry.com/your-username/jib-example:latest
```

**注意**：如果不指定命令行参数，将使用 `build.gradle` 中 `jib` 配置块定义的默认值：

* `jib.from.image` (默认: `eclipse-temurin:17-jre-alpine`)
* `jib.to.image` (默认: `jib-example:latest`)

### 3. 运行容器

```bash
docker run -d -p 8080:8080 --name jib-example jib-example:latest
```

### 4. 运行单元测试

```bash
./gradlew test
```

Windows 系统使用：

```bash
gradlew.bat test
```

### 5. 测试应用

```bash
# 健康检查
curl http://localhost:8080/api/health

# Hello接口
curl http://localhost:8080/api/hello

# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "name": "测试用户"
  }'

# 获取所有用户
curl http://localhost:8080/api/users

# 根据ID获取用户
curl http://localhost:8080/api/users/1

# 根据用户名获取用户
curl http://localhost:8080/api/users/username/testuser

# 更新用户
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com",
    "name": "更新用户"
  }'

# 删除用户
curl -X DELETE http://localhost:8080/api/users/1
```

## API 端点

### Hello接口

* `GET /api/hello` - 返回欢迎消息和时间戳
* `GET /api/health` - 健康检查端点

### 用户管理接口

* `POST /api/users` - 创建用户
* `GET /api/users` - 获取所有用户列表
* `GET /api/users/{id}` - 根据ID获取用户
* `GET /api/users/username/{username}` - 根据用户名获取用户
* `PUT /api/users/{id}` - 更新用户信息
* `DELETE /api/users/{id}` - 删除用户

### Actuator端点

* `GET /actuator/health` - Spring Boot Actuator 健康检查
* `GET /h2-console` - H2数据库控制台（开发环境）

## 数据库配置

项目使用H2内存数据库，配置信息在 `application.properties` 中：

* **数据库URL**: `jdbc:h2:mem:testdb`
* **用户名**: `sa`
* **密码**: 空
* **JPA自动更新表结构**: `spring.jpa.hibernate.ddl-auto=update`

可以通过 `http://localhost:8080/h2-console` 访问H2控制台（开发环境）。

## 四层架构说明

### 1. Entity层（实体层）

* **位置**: `src/main/java/com/example/jibexample/entity/`
* **说明**: 定义数据实体类，使用JPA注解进行ORM映射
* **示例**: `User.java` - 用户实体类

### 2. DAO层（数据访问层）

* **位置**: `src/main/java/com/example/jibexample/dao/`
* **说明**: 数据访问接口，继承Spring Data JPA的`JpaRepository`
* **示例**: `UserRepository.java` - 用户数据访问接口

### 3. Service层（业务逻辑层）

* **位置**: `src/main/java/com/example/jibexample/service/`
* **说明**: 业务逻辑处理，包含数据验证、异常处理等
* **示例**: `UserService.java` - 用户业务逻辑服务

### 4. Controller层（控制器层）

* **位置**: `src/main/java/com/example/jibexample/controller/`
* **说明**: REST API控制器，处理HTTP请求和响应
* **示例**: `UserController.java` - 用户管理REST控制器

## 单元测试

项目包含完整的单元测试用例：

### Service层测试

* **文件**: `src/test/java/com/example/jibexample/service/UserServiceTest.java`
* **测试内容**:  
   * 用户创建（成功/失败场景）  
   * 用户查询（ID/用户名）  
   * 用户更新  
   * 用户删除  
   * 异常处理

### Controller层测试

* **文件**: `src/test/java/com/example/jibexample/controller/UserControllerTest.java`
* **测试内容**:  
   * REST API端点测试  
   * HTTP状态码验证  
   * JSON响应格式验证  
   * 异常响应处理

运行测试：

```bash
./gradlew test
```

Windows 系统使用：

```bash
gradlew.bat test
```

## Jib 配置说明

在 `build.gradle` 中配置了 Jib Gradle 插件，基础镜像和目标镜像可以通过命令行参数动态指定：

### 默认配置（在 build.gradle 的 jib 配置块中定义）

* **基础镜像**: `eclipse-temurin:17-jre-alpine` (轻量级 JRE)
* **目标镜像**: `jib-example:latest`
* **JVM参数**: `-Xms512m -Xmx512m`
* **端口**: `8080`
* **格式**: `docker`

### 通过命令行参数覆盖镜像配置

可以在执行 Jib 任务时通过 Gradle 项目属性覆盖默认配置：

```bash
# 覆盖基础镜像
./gradlew jibDockerBuild -Pjib.from.image=openjdk:17-jre-slim

# 覆盖目标镜像
./gradlew jibDockerBuild -Pjib.to.image=my-registry.com/my-app:v1.0.0

# 同时覆盖基础镜像和目标镜像
./gradlew jibDockerBuild \
  -Pjib.from.image=eclipse-temurin:17-jre-alpine \
  -Pjib.to.image=registry.example.com/jib-example:1.0.0
```

**参数说明**：

* `-Pjib.from.image`: 指定基础镜像（FROM镜像）
* `-Pjib.to.image`: 指定目标镜像名称和标签

这种方式使得在不同环境（开发、测试、生产）中使用不同的镜像配置变得更加灵活，无需修改 `build.gradle` 文件。

## Jib 的优势

1. **无需 Dockerfile** - Jib 自动处理镜像构建
2. **无需 Docker daemon** - 可以直接推送到远程仓库
3. **分层优化** - 依赖和代码分离，加快构建速度
4. **可重现构建** - 相同输入产生相同输出

## 开发说明

### 运行应用

```bash
# 编译项目
./gradlew clean compileJava

# 运行应用
./gradlew bootRun
```

Windows 系统使用：

```bash
gradlew.bat clean compileJava
gradlew.bat bootRun
```

应用将在 `http://localhost:8080` 启动。

### 测试覆盖率

项目包含完整的单元测试，覆盖了Service层和Controller层的主要功能。建议在开发新功能时同步添加相应的测试用例。

### 代码规范

* 使用标准的Java命名规范
* 遵循Spring Boot最佳实践
* 保持代码注释清晰
* 确保单元测试覆盖主要业务逻辑

## Gradle vs Maven

本项目使用 Gradle 构建，相比 Maven 的优势：

* **更简洁的配置**: Gradle 使用 Groovy/Kotlin DSL，配置更简洁
* **更快的构建**: Gradle 的增量构建和缓存机制
* **更好的依赖管理**: 更灵活的依赖解析策略
* **更强大的插件系统**: 丰富的插件生态

## 参考资源

* [Jib GitHub](https://github.com/GoogleContainerTools/jib)
* [Jib Gradle Plugin 文档](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)
* [Spring Boot 文档](https://spring.io/projects/spring-boot)
* [Spring Data JPA 文档](https://spring.io/projects/spring-data-jpa)
* [H2 Database 文档](https://www.h2database.com/html/main.html)
* [Gradle 文档](https://docs.gradle.org/)

## 许可证

本项目仅用于示例和学习目的。
