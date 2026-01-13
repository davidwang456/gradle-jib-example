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

#### 方式三：直接推送到远程仓库（使用 Jib 认证）

Jib 支持自己的认证方式，**无需使用 `docker login`**。有以下几种认证方式：

##### 方式 3.1：通过命令行参数传递凭证（推荐用于临时使用）

```bash
# 推送到 Docker Hub
./gradlew jib \
  -Pjib.to.image=your-dockerhub-username/jib-example:latest \
  -Pjib.to.auth.username=your-dockerhub-username \
  -Pjib.to.auth.password=your-dockerhub-password

# 推送到私有仓库
./gradlew jib \
  -Pjib.to.image=your-registry.com/your-username/jib-example:latest \
  -Pjib.to.auth.username=your-username \
  -Pjib.to.auth.password=your-password
```

Windows PowerShell 使用：

```powershell
# 推送到 Docker Hub
gradlew.bat jib `
  -Pjib.to.image=your-dockerhub-username/jib-example:latest `
  -Pjib.to.auth.username=your-dockerhub-username `
  -Pjib.to.auth.password=your-dockerhub-password
```

##### 方式 3.2：通过环境变量传递凭证（推荐用于 CI/CD）

```bash
# 设置环境变量
export JIB_AUTH_USERNAME=your-dockerhub-username
export JIB_AUTH_PASSWORD=your-dockerhub-password

# 然后执行构建（无需在命令行中传递密码）
./gradlew jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

Windows PowerShell 使用：

```powershell
# 设置环境变量
$env:JIB_AUTH_USERNAME="your-dockerhub-username"
$env:JIB_AUTH_PASSWORD="your-dockerhub-password"

# 然后执行构建
gradlew.bat jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

Windows CMD 使用：

```cmd
set JIB_AUTH_USERNAME=your-dockerhub-username
set JIB_AUTH_PASSWORD=your-dockerhub-password
gradlew.bat jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

##### 方式 3.3：使用 gradle.properties 文件（不推荐，密码会暴露）

在项目根目录创建或编辑 `gradle.properties` 文件（**注意：不要提交到 Git**）：

```properties
jib.to.auth.username=your-dockerhub-username
jib.to.auth.password=your-dockerhub-password
```

然后将 `gradle.properties` 添加到 `.gitignore`：

```gitignore
gradle.properties
```

之后可以直接执行：

```bash
./gradlew jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

##### 方式 3.4：使用 Docker 凭证助手（Credential Helper）

Jib 也支持使用 Docker 凭证助手，如果已配置 Docker credential helper：

```bash
# 使用 Docker credential helper（需要先配置）
./gradlew jib -Pjib.to.image=your-registry.com/your-username/jib-example:latest
```

**注意**：
* 如果不指定命令行参数，将使用 `build.gradle` 中 `jib` 配置块定义的默认值
* `jib.from.image` (默认: `eclipse-temurin:17-jre-alpine`)
* `jib.to.image` (默认: `jib-example:latest`)
* 认证优先级：命令行参数 > 环境变量 > gradle.properties

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
* **认证**: 支持通过项目属性或环境变量配置

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
* `-Pjib.to.auth.username`: 指定认证用户名（可选，也可通过环境变量 `JIB_AUTH_USERNAME`）
* `-Pjib.to.auth.password`: 指定认证密码（可选，也可通过环境变量 `JIB_AUTH_PASSWORD`）

### Jib 认证配置详解

Jib 支持多种认证方式，**无需使用 `docker login`**：

#### 1. 认证优先级

Jib 按以下优先级查找认证信息：
1. **命令行参数** (`-Pjib.to.auth.username` / `-Pjib.to.auth.password`)
2. **环境变量** (`JIB_AUTH_USERNAME` / `JIB_AUTH_PASSWORD`)
3. **gradle.properties** 文件（不推荐，密码会暴露）

#### 2. 认证配置示例

**推送到 Docker Hub：**

```bash
# 方式1: 命令行参数
./gradlew jib \
  -Pjib.to.image=your-username/jib-example:latest \
  -Pjib.to.auth.username=your-username \
  -Pjib.to.auth.password=your-password

# 方式2: 环境变量（更安全）
export JIB_AUTH_USERNAME=your-username
export JIB_AUTH_PASSWORD=your-password
./gradlew jib -Pjib.to.image=your-username/jib-example:latest
```

**推送到私有仓库：**

```bash
# 私有仓库格式: registry.example.com/namespace/image:tag
./gradlew jib \
  -Pjib.to.image=registry.example.com/namespace/jib-example:latest \
  -Pjib.to.auth.username=your-username \
  -Pjib.to.auth.password=your-password
```

#### 3. 安全建议

* ✅ **推荐**: 使用环境变量传递密码，避免在命令行历史中暴露
* ✅ **推荐**: 在 CI/CD 中使用密钥管理工具（如 GitHub Secrets、GitLab CI Variables）
* ❌ **不推荐**: 在 `gradle.properties` 中硬编码密码
* ❌ **不推荐**: 在命令行中直接传递密码（会出现在进程列表中）

#### 4. CI/CD 集成示例

**GitHub Actions:**

```yaml
- name: Build and push Docker image
  env:
    JIB_AUTH_USERNAME: ${{ secrets.DOCKER_USERNAME }}
    JIB_AUTH_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
  run: |
    ./gradlew jib -Pjib.to.image=your-username/jib-example:${{ github.sha }}
```

**GitLab CI:**

详细说明请参考下面的 [GitLab CI/CD 集成](#gitlab-cicd-集成) 章节。

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

## GitLab CI/CD 集成

本项目提供了完整的 GitLab CI/CD 配置示例，支持使用 Jib 构建和推送 Docker 镜像。

### 配置文件说明

项目包含以下 GitLab CI 配置文件：

1. **`.gitlab-ci.yml`** - 推送到 GitLab Container Registry（推荐）
2. **`.gitlab-ci-dockerhub.yml.example`** - 推送到 Docker Hub 的示例配置
3. **`.gitlab-ci-private-registry.yml.example`** - 推送到私有仓库的示例配置

### 方式一：使用 GitLab Container Registry（推荐）

GitLab 提供了内置的容器镜像仓库，无需额外配置。

#### 1. 配置 CI/CD Variables

GitLab 会自动提供以下内置变量，无需手动配置：

* `CI_REGISTRY` - GitLab Container Registry 地址
* `CI_REGISTRY_USER` - Registry 用户名（自动生成）
* `CI_REGISTRY_PASSWORD` - Registry 密码（自动生成）
* `CI_REGISTRY_IMAGE` - 镜像完整路径（格式: `registry.gitlab.com/group/project`）

#### 2. 使用默认配置

直接使用项目根目录的 `.gitlab-ci.yml` 文件即可：

```yaml
# .gitlab-ci.yml 已配置好，会自动使用内置变量
package:
  before_script:
    - export JIB_AUTH_USERNAME="${CI_REGISTRY_USER}"
    - export JIB_AUTH_PASSWORD="${CI_REGISTRY_PASSWORD}"
  script:
    - ./gradlew jib -Pjib.to.image="${CI_REGISTRY_IMAGE}:${CI_COMMIT_SHORT_SHA}"
```

#### 3. 推送结果

镜像会自动推送到：`registry.gitlab.com/your-group/your-project:commit-sha`

### 方式二：推送到 Docker Hub

#### 1. 配置 CI/CD Variables

在 GitLab 项目设置中添加以下变量：

**路径**: `Settings` → `CI/CD` → `Variables` → `Expand`

需要添加的变量：

| Key | Value | 说明 | Protected | Masked |
|-----|-------|------|-----------|--------|
| `DOCKER_HUB_USERNAME` | `your-dockerhub-username` | Docker Hub 用户名 | ✅ | ❌ |
| `DOCKER_HUB_PASSWORD` | `your-dockerhub-token` | Docker Hub 访问令牌（推荐）或密码 | ✅ | ✅ |

**安全建议**：
* ✅ 使用 Docker Hub 访问令牌（Access Token）而不是密码
* ✅ 勾选 `Protected`（仅在保护分支/标签中使用）
* ✅ 勾选 `Masked`（在日志中隐藏）

#### 2. 使用示例配置

复制 `.gitlab-ci-dockerhub.yml.example` 为 `.gitlab-ci.yml`：

```bash
cp .gitlab-ci-dockerhub.yml.example .gitlab-ci.yml
```

#### 3. 配置说明

```yaml
package:
  before_script:
    # 从 GitLab CI/CD Variables 读取认证信息
    - export JIB_AUTH_USERNAME="${DOCKER_HUB_USERNAME}"
    - export JIB_AUTH_PASSWORD="${DOCKER_HUB_PASSWORD}"
  script:
    - ./gradlew jib -Pjib.to.image="${DOCKER_HUB_USERNAME}/jib-example:${CI_COMMIT_SHORT_SHA}"
```

### 方式三：推送到私有镜像仓库

#### 1. 配置 CI/CD Variables

在 GitLab 项目设置中添加以下变量：

| Key | Value | 说明 | Protected | Masked |
|-----|-------|------|-----------|--------|
| `REGISTRY_USERNAME` | `your-registry-username` | 私有仓库用户名 | ✅ | ❌ |
| `REGISTRY_PASSWORD` | `your-registry-password` | 私有仓库密码或令牌 | ✅ | ✅ |

#### 2. 使用示例配置

复制 `.gitlab-ci-private-registry.yml.example` 为 `.gitlab-ci.yml` 并修改：

```yaml
variables:
  PRIVATE_REGISTRY: "registry.example.com"  # 修改为你的私有仓库地址
  REGISTRY_IMAGE: "${PRIVATE_REGISTRY}/namespace/jib-example"  # 修改命名空间

package:
  before_script:
    - export JIB_AUTH_USERNAME="${REGISTRY_USERNAME}"
    - export JIB_AUTH_PASSWORD="${REGISTRY_PASSWORD}"
  script:
    - ./gradlew jib -Pjib.to.image="${REGISTRY_IMAGE}:${CI_COMMIT_SHORT_SHA}"
```

### GitLab CI Pipeline 阶段说明

默认配置包含以下阶段：

1. **build** - 编译项目
2. **test** - 运行单元测试
3. **package** - 使用 Jib 构建并推送 Docker 镜像
4. **deploy** - 部署阶段（示例，需要根据实际情况配置）

### 常用 GitLab CI 变量

| 变量 | 说明 | 示例值 |
|------|------|--------|
| `CI_COMMIT_SHORT_SHA` | 提交 SHA（短） | `a1b2c3d` |
| `CI_COMMIT_SHA` | 提交 SHA（完整） | `a1b2c3d4e5f6...` |
| `CI_COMMIT_REF_NAME` | 分支或标签名 | `main` |
| `CI_DEFAULT_BRANCH` | 默认分支 | `main` |
| `CI_REGISTRY_IMAGE` | GitLab Container Registry 镜像路径 | `registry.gitlab.com/group/project` |

### 最佳实践

1. **使用访问令牌而非密码**
   * Docker Hub: 使用 Access Token
   * 私有仓库: 使用只读或只写令牌

2. **保护敏感变量**
   * 勾选 `Protected` - 仅在保护分支使用
   * 勾选 `Masked` - 在日志中隐藏

3. **使用标签策略**
   ```yaml
   # 使用提交 SHA 作为标签
   DOCKER_TAG: "${CI_COMMIT_SHORT_SHA}"
   
   # 主分支同时打 latest 标签
   if [ "$CI_COMMIT_BRANCH" == "$CI_DEFAULT_BRANCH" ]; then
     ./gradlew jib -Pjib.to.image="${IMAGE}:latest"
   fi
   ```

4. **缓存 Gradle 依赖**
   ```yaml
   cache:
     paths:
       - .gradle/
   ```

5. **并行执行任务**
   ```yaml
   build:
     stage: build
     # ...
   test:
     stage: test
     # ...
   # build 和 test 可以并行执行
   ```

### 故障排查

**问题1**: 认证失败
```
Error: Failed to execute goal com.google.cloud.tools:jib-maven-plugin
```

**解决方案**:
* 检查 CI/CD Variables 是否正确设置
* 确认变量名与 `.gitlab-ci.yml` 中的一致
* 检查 `Masked` 变量是否包含特殊字符（如 `@`、`#`）

**问题2**: 镜像推送失败
```
Failed to push image: unauthorized
```

**解决方案**:
* 确认用户名和密码/令牌正确
* 检查镜像仓库地址格式是否正确
* 确认账户有推送权限

**问题3**: Gradle Wrapper 权限问题
```
Permission denied: ./gradlew
```

**解决方案**:
```yaml
before_script:
  - chmod +x ./gradlew
```

## 参考资源

* [Jib GitHub](https://github.com/GoogleContainerTools/jib)
* [Jib Gradle Plugin 文档](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)
* [Spring Boot 文档](https://spring.io/projects/spring-boot)
* [Spring Data JPA 文档](https://spring.io/projects/spring-data-jpa)
* [H2 Database 文档](https://www.h2database.com/html/main.html)
* [Gradle 文档](https://docs.gradle.org/)

## 许可证

本项目仅用于示例和学习目的。
