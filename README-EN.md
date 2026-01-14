# Gradle Jib Example Project

This is an example project using Gradle and Jib to package a Spring Boot application into a Docker image.

## Project Features

* **Spring Boot 3.2.0**: Modern Spring Boot framework
* **Spring Data JPA**: Data access layer
* **H2 Database**: In-memory database
* **JUnit 5**: Unit testing framework
* **Mockito**: Mock testing framework
* **Gradle**: Project build tool
* **Jib**: Google's container image building tool

## Prerequisites

* JDK 17 or higher
* Gradle 8.5 or higher (project includes Gradle Wrapper)
* Docker (optional, if you want to push images to local Docker daemon)

## Usage

### 1. Build the Project

```bash
./gradlew build
```

For Windows:

```bash
gradlew.bat build
```

### 2. Build Docker Image with Jib

#### Method 1: Build and Load to Local Docker (Recommended)

Using default configuration:

```bash
./gradlew jibDockerBuild
```

For Windows:

```bash
gradlew.bat jibDockerBuild
```

Using custom image configuration:

```bash
# Specify base image and target image
./gradlew jibDockerBuild \
  -Pjib.from.image=eclipse-temurin:17-jre-alpine \
  -Pjib.to.image=my-custom-image:1.0.0

# Or only override target image
./gradlew jibDockerBuild -Pjib.to.image=my-registry.com/my-app:v1.0.0
```

This will build the image locally and load it into the Docker daemon.

#### Method 2: Build Docker Tar File

Using default configuration:

```bash
./gradlew jibBuildTar
```

Using custom image configuration:

```bash
./gradlew jibBuildTar \
  -Pjib.from.image=eclipse-temurin:17-jre-alpine \
  -Pjib.to.image=my-app:latest
```

This will generate a tar file: `build/jib-image.tar`, which can be loaded using:

```bash
docker load -i build/jib-image.tar
```

#### Method 3: Push Directly to Remote Registry (Using Jib Authentication)

Jib supports its own authentication methods, **no need to use `docker login`**. There are several authentication methods:

##### Method 3.1: Pass Credentials via Command Line Arguments (Recommended for temporary use)

```bash
# Push to Docker Hub
./gradlew jib \
  -Pjib.to.image=your-dockerhub-username/jib-example:latest \
  -Pjib.to.auth.username=your-dockerhub-username \
  -Pjib.to.auth.password=your-dockerhub-password

# Push to private registry
./gradlew jib \
  -Pjib.to.image=your-registry.com/your-username/jib-example:latest \
  -Pjib.to.auth.username=your-username \
  -Pjib.to.auth.password=your-password
```

For Windows PowerShell:

```powershell
# Push to Docker Hub
gradlew.bat jib `
  -Pjib.to.image=your-dockerhub-username/jib-example:latest `
  -Pjib.to.auth.username=your-dockerhub-username `
  -Pjib.to.auth.password=your-dockerhub-password
```

##### Method 3.2: Pass Credentials via Environment Variables (Recommended for CI/CD)

```bash
# Set environment variables
export JIB_AUTH_USERNAME=your-dockerhub-username
export JIB_AUTH_PASSWORD=your-dockerhub-password

# Then execute build (no need to pass password in command line)
./gradlew jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

For Windows PowerShell:

```powershell
# Set environment variables
$env:JIB_AUTH_USERNAME="your-dockerhub-username"
$env:JIB_AUTH_PASSWORD="your-dockerhub-password"

# Then execute build
gradlew.bat jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

For Windows CMD:

```cmd
set JIB_AUTH_USERNAME=your-dockerhub-username
set JIB_AUTH_PASSWORD=your-dockerhub-password
gradlew.bat jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

##### Method 3.3: Use gradle.properties File (Not Recommended, Password Will Be Exposed)

Create or edit `gradle.properties` file in the project root directory (**Note: Do not commit to Git**):

```properties
jib.to.auth.username=your-dockerhub-username
jib.to.auth.password=your-dockerhub-password
```

Then add `gradle.properties` to `.gitignore`:

```gitignore
gradle.properties
```

After that, you can execute directly:

```bash
./gradlew jib -Pjib.to.image=your-dockerhub-username/jib-example:latest
```

##### Method 3.4: Use Docker Credential Helper

Jib also supports using Docker credential helper, if Docker credential helper is already configured:

```bash
# Use Docker credential helper (needs to be configured first)
./gradlew jib -Pjib.to.image=your-registry.com/your-username/jib-example:latest
```

**Note**:
* If command line arguments are not specified, default values defined in the `jib` configuration block in `build.gradle` will be used
* `jib.from.image` (default: `eclipse-temurin:17-jre-alpine`)
* `jib.to.image` (default: `jib-example:latest`)
* Authentication priority: Command line arguments > Environment variables > gradle.properties

### 3. Run Container

```bash
docker run -d -p 8080:8080 --name jib-example jib-example:latest
```

### 4. Run Unit Tests

```bash
./gradlew test
```

For Windows:

```bash
gradlew.bat test
```

### 5. Test the Application

```bash
# Health check
curl http://localhost:8080/api/health

# Hello endpoint
curl http://localhost:8080/api/hello

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "name": "Test User"
  }'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Get user by username
curl http://localhost:8080/api/users/username/testuser

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com",
    "name": "Updated User"
  }'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1
```

## API Endpoints

### Hello Endpoints

* `GET /api/hello` - Returns welcome message and timestamp
* `GET /api/health` - Health check endpoint

### User Management Endpoints

* `POST /api/users` - Create user
* `GET /api/users` - Get all users list
* `GET /api/users/{id}` - Get user by ID
* `GET /api/users/username/{username}` - Get user by username
* `PUT /api/users/{id}` - Update user information
* `DELETE /api/users/{id}` - Delete user

### Actuator Endpoints

* `GET /actuator/health` - Spring Boot Actuator health check
* `GET /h2-console` - H2 database console (development environment)

## Database Configuration

The project uses H2 in-memory database, configuration information is in `application.properties`:

* **Database URL**: `jdbc:h2:mem:testdb`
* **Username**: `sa`
* **Password**: empty
* **JPA Auto Update Schema**: `spring.jpa.hibernate.ddl-auto=update`

You can access the H2 console at `http://localhost:8080/h2-console` (development environment).

## Four-Layer Architecture

### 1. Entity Layer

* **Location**: `src/main/java/com/example/jibexample/entity/`
* **Description**: Defines data entity classes using JPA annotations for ORM mapping
* **Example**: `User.java` - User entity class

### 2. DAO Layer (Data Access Layer)

* **Location**: `src/main/java/com/example/jibexample/dao/`
* **Description**: Data access interfaces, extending Spring Data JPA's `JpaRepository`
* **Example**: `UserRepository.java` - User data access interface

### 3. Service Layer (Business Logic Layer)

* **Location**: `src/main/java/com/example/jibexample/service/`
* **Description**: Business logic processing, including data validation, exception handling, etc.
* **Example**: `UserService.java` - User business logic service

### 4. Controller Layer

* **Location**: `src/main/java/com/example/jibexample/controller/`
* **Description**: REST API controllers, handling HTTP requests and responses
* **Example**: `UserController.java` - User management REST controller

## Unit Tests

The project includes comprehensive unit test cases:

### Service Layer Tests

* **File**: `src/test/java/com/example/jibexample/service/UserServiceTest.java`
* **Test Content**:  
   * User creation (success/failure scenarios)  
   * User queries (by ID/username)  
   * User updates  
   * User deletion  
   * Exception handling

### Controller Layer Tests

* **File**: `src/test/java/com/example/jibexample/controller/UserControllerTest.java`
* **Test Content**:  
   * REST API endpoint tests  
   * HTTP status code validation  
   * JSON response format validation  
   * Exception response handling

Run tests:

```bash
./gradlew test
```

For Windows:

```bash
gradlew.bat test
```

## Jib Configuration

The Jib Gradle plugin is configured in `build.gradle`, base image and target image can be dynamically specified via command line arguments:

### Default Configuration (Defined in jib Configuration Block in build.gradle)

* **Base Image**: `eclipse-temurin:17-jre-alpine` (lightweight JRE)
* **Target Image**: `jib-example:latest`
* **JVM Parameters**: `-Xms512m -Xmx512m`
* **Port**: `8080`
* **Format**: `docker`
* **Authentication**: Supports configuration via project properties or environment variables

### Override Image Configuration via Command Line Arguments

You can override default configuration via Gradle project properties when executing Jib tasks:

```bash
# Override base image
./gradlew jibDockerBuild -Pjib.from.image=openjdk:17-jre-slim

# Override target image
./gradlew jibDockerBuild -Pjib.to.image=my-registry.com/my-app:v1.0.0

# Override both base image and target image
./gradlew jibDockerBuild \
  -Pjib.from.image=eclipse-temurin:17-jre-alpine \
  -Pjib.to.image=registry.example.com/jib-example:1.0.0
```

**Parameter Description**:

* `-Pjib.from.image`: Specify base image (FROM image)
* `-Pjib.to.image`: Specify target image name and tag
* `-Pjib.to.auth.username`: Specify authentication username (optional, can also use environment variable `JIB_AUTH_USERNAME`)
* `-Pjib.to.auth.password`: Specify authentication password (optional, can also use environment variable `JIB_AUTH_PASSWORD`)

### Jib Authentication Configuration

Jib supports multiple authentication methods, **no need to use `docker login`**:

#### 1. Authentication Priority

Jib looks for authentication information in the following priority:
1. **Command line arguments** (`-Pjib.to.auth.username` / `-Pjib.to.auth.password`)
2. **Environment variables** (`JIB_AUTH_USERNAME` / `JIB_AUTH_PASSWORD`)
3. **gradle.properties** file (not recommended, password will be exposed)

#### 2. Authentication Configuration Examples

**Push to Docker Hub:**

```bash
# Method 1: Command line arguments
./gradlew jib \
  -Pjib.to.image=your-username/jib-example:latest \
  -Pjib.to.auth.username=your-username \
  -Pjib.to.auth.password=your-password

# Method 2: Environment variables (more secure)
export JIB_AUTH_USERNAME=your-username
export JIB_AUTH_PASSWORD=your-password
./gradlew jib -Pjib.to.image=your-username/jib-example:latest
```

**Push to Private Registry:**

```bash
# Private registry format: registry.example.com/namespace/image:tag
./gradlew jib \
  -Pjib.to.image=registry.example.com/namespace/jib-example:latest \
  -Pjib.to.auth.username=your-username \
  -Pjib.to.auth.password=your-password
```

#### 3. Security Recommendations

* ✅ **Recommended**: Use environment variables to pass passwords, avoid exposure in command history
* ✅ **Recommended**: Use secret management tools in CI/CD (such as GitHub Secrets, GitLab CI Variables)
* ❌ **Not Recommended**: Hardcode passwords in `gradle.properties`
* ❌ **Not Recommended**: Pass passwords directly in command line (will appear in process list)

#### 4. CI/CD Integration Examples

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

For detailed instructions, please refer to the [GitLab CI/CD Integration](#gitlab-cicd-integration) section below.

This approach makes it more flexible to use different image configurations in different environments (development, testing, production) without modifying the `build.gradle` file.

## Advantages of Jib

1. **No Dockerfile Required** - Jib automatically handles image building
2. **No Docker Daemon Required** - Can push directly to remote registry
3. **Layer Optimization** - Dependencies and code are separated, speeding up builds
4. **Reproducible Builds** - Same input produces same output

## Development Guide

### Run the Application

```bash
# Compile project
./gradlew clean compileJava

# Run application
./gradlew bootRun
```

For Windows:

```bash
gradlew.bat clean compileJava
gradlew.bat bootRun
```

The application will start at `http://localhost:8080`.

### Test Coverage

The project includes comprehensive unit tests covering main functionality of Service and Controller layers. It is recommended to add corresponding test cases when developing new features.

### Code Standards

* Use standard Java naming conventions
* Follow Spring Boot best practices
* Keep code comments clear
* Ensure unit tests cover main business logic

## Gradle vs Maven

This project uses Gradle for building, advantages over Maven:

* **More Concise Configuration**: Gradle uses Groovy/Kotlin DSL, configuration is more concise
* **Faster Builds**: Gradle's incremental build and caching mechanisms
* **Better Dependency Management**: More flexible dependency resolution strategies
* **More Powerful Plugin System**: Rich plugin ecosystem

## GitLab CI/CD Integration

This project provides complete GitLab CI/CD configuration examples, supporting building and pushing Docker images using Jib.

### Configuration Files

The project includes the following GitLab CI configuration files:

1. **`.gitlab-ci.yml`** - Push to GitLab Container Registry (Recommended)
2. **`.gitlab-ci-dockerhub.yml.example`** - Example configuration for pushing to Docker Hub
3. **`.gitlab-ci-private-registry.yml.example`** - Example configuration for pushing to private registry

### Method 1: Use GitLab Container Registry (Recommended)

GitLab provides a built-in container image registry, no additional configuration needed.

#### 1. Configure CI/CD Variables

GitLab automatically provides the following built-in variables, no manual configuration needed:

* `CI_REGISTRY` - GitLab Container Registry address
* `CI_REGISTRY_USER` - Registry username (auto-generated)
* `CI_REGISTRY_PASSWORD` - Registry password (auto-generated)
* `CI_REGISTRY_IMAGE` - Complete image path (format: `registry.gitlab.com/group/project`)

#### 2. Use Default Configuration

Simply use the `.gitlab-ci.yml` file in the project root directory:

```yaml
# .gitlab-ci.yml is already configured, will automatically use built-in variables
package:
  before_script:
    # Convert CI_REGISTRY_USER to lowercase before passing to JIB_AUTH_USERNAME
    - export JIB_AUTH_USERNAME=$(echo "${CI_REGISTRY_USER}" | tr '[:upper:]' '[:lower:]')
    - export JIB_AUTH_PASSWORD="${CI_REGISTRY_PASSWORD}"
  script:
    - ./gradlew jib -Pjib.to.image="${CI_REGISTRY_IMAGE}:${CI_COMMIT_SHORT_SHA}"
```

#### 3. Push Result

Images will be automatically pushed to: `registry.gitlab.com/your-group/your-project:commit-sha`

### Method 2: Push to Docker Hub

#### 1. Configure CI/CD Variables

Add the following variables in GitLab project settings:

**Path**: `Settings` → `CI/CD` → `Variables` → `Expand`

Variables to add:

| Key | Value | Description | Protected | Masked |
|-----|-------|-------------|-----------|--------|
| `DOCKER_HUB_USERNAME` | `your-dockerhub-username` | Docker Hub username | ✅ | ❌ |
| `DOCKER_HUB_PASSWORD` | `your-dockerhub-token` | Docker Hub access token (recommended) or password | ✅ | ✅ |

**Security Recommendations**:
* ✅ Use Docker Hub access token (Access Token) instead of password
* ✅ Check `Protected` (only use in protected branches/tags)
* ✅ Check `Masked` (hide in logs)

#### 2. Use Example Configuration

Copy `.gitlab-ci-dockerhub.yml.example` to `.gitlab-ci.yml`:

```bash
cp .gitlab-ci-dockerhub.yml.example .gitlab-ci.yml
```

#### 3. Configuration Description

```yaml
package:
  before_script:
    # Read authentication information from GitLab CI/CD Variables
    # Convert username to lowercase (some registries require lowercase usernames)
    - export JIB_AUTH_USERNAME=$(echo "${DOCKER_HUB_USERNAME}" | tr '[:upper:]' '[:lower:]')
    - export JIB_AUTH_PASSWORD="${DOCKER_HUB_PASSWORD}"
  script:
    - ./gradlew jib -Pjib.to.image="${DOCKER_HUB_USERNAME}/jib-example:${CI_COMMIT_SHORT_SHA}"
```

### Method 3: Push to Private Image Registry

#### 1. Configure CI/CD Variables

Add the following variables in GitLab project settings:

| Key | Value | Description | Protected | Masked |
|-----|-------|-------------|-----------|--------|
| `REGISTRY_USERNAME` | `your-registry-username` | Private registry username | ✅ | ❌ |
| `REGISTRY_PASSWORD` | `your-registry-password` | Private registry password or token | ✅ | ✅ |

#### 2. Use Example Configuration

Copy `.gitlab-ci-private-registry.yml.example` to `.gitlab-ci.yml` and modify:

```yaml
variables:
  PRIVATE_REGISTRY: "registry.example.com"  # Modify to your private registry address
  REGISTRY_IMAGE: "${PRIVATE_REGISTRY}/namespace/jib-example"  # Modify namespace

package:
  before_script:
    # Convert username to lowercase (some registries require lowercase usernames)
    - export JIB_AUTH_USERNAME=$(echo "${REGISTRY_USERNAME}" | tr '[:upper:]' '[:lower:]')
    - export JIB_AUTH_PASSWORD="${REGISTRY_PASSWORD}"
  script:
    - ./gradlew jib -Pjib.to.image="${REGISTRY_IMAGE}:${CI_COMMIT_SHORT_SHA}"
```

### GitLab CI Pipeline Stages

Default configuration includes the following stages:

1. **build** - Compile project
2. **test** - Run unit tests
3. **package** - Build and push Docker image using Jib
4. **deploy** - Deployment stage (example, needs to be configured according to actual situation)

### Common GitLab CI Variables

| Variable | Description | Example Value |
|----------|-------------|---------------|
| `CI_COMMIT_SHORT_SHA` | Commit SHA (short) | `a1b2c3d` |
| `CI_COMMIT_SHA` | Commit SHA (full) | `a1b2c3d4e5f6...` |
| `CI_COMMIT_REF_NAME` | Branch or tag name | `main` |
| `CI_DEFAULT_BRANCH` | Default branch | `main` |
| `CI_REGISTRY_IMAGE` | GitLab Container Registry image path | `registry.gitlab.com/group/project` |

### Best Practices

1. **Use Access Tokens Instead of Passwords**
   * Docker Hub: Use Access Token
   * Private Registry: Use read-only or write-only tokens

2. **Protect Sensitive Variables**
   * Check `Protected` - Only use in protected branches
   * Check `Masked` - Hide in logs

3. **Use Tagging Strategy**
   ```yaml
   # Use commit SHA as tag
   DOCKER_TAG: "${CI_COMMIT_SHORT_SHA}"
   
   # Main branch also tags as latest
   if [ "$CI_COMMIT_BRANCH" == "$CI_DEFAULT_BRANCH" ]; then
     ./gradlew jib -Pjib.to.image="${IMAGE}:latest"
   fi
   ```

4. **Cache Gradle Dependencies**
   ```yaml
   cache:
     paths:
       - .gradle/
   ```

5. **Parallel Task Execution**
   ```yaml
   build:
     stage: build
     # ...
   test:
     stage: test
     # ...
   # build and test can execute in parallel
   ```

### Troubleshooting

**Issue 1**: Authentication Failed
```
Error: Failed to execute goal com.google.cloud.tools:jib-maven-plugin
```

**Solution**:
* Check if CI/CD Variables are correctly set
* Confirm variable names match those in `.gitlab-ci.yml`
* Check if `Masked` variables contain special characters (such as `@`, `#`)

**Issue 2**: Image Push Failed
```
Failed to push image: unauthorized
```

**Solution**:
* Confirm username and password/token are correct
* Check if image registry address format is correct
* Confirm account has push permissions

**Issue 3**: Gradle Wrapper Permission Issue
```
Permission denied: ./gradlew
```

**Solution**:
```yaml
before_script:
  - chmod +x ./gradlew
```

## Reference Resources

* [Jib GitHub](https://github.com/GoogleContainerTools/jib)
* [Jib Gradle Plugin Documentation](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)
* [Spring Boot Documentation](https://spring.io/projects/spring-boot)
* [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
* [H2 Database Documentation](https://www.h2database.com/html/main.html)
* [Gradle Documentation](https://docs.gradle.org/)

## License

This project is for example and learning purposes only.
