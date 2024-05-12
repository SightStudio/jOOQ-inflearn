# 섹션 4-1. JPA 엔티티를 기반으로 DSL 만들기

- Docs
    - JPADatabase: Code generation from entities
        - https://www.jooq.org/doc/latest/manual/code-generation/codegen-jpa

## 0. 필독

해당 내역을 동일하게 작업하기 전에 이전에 작업한 내용들을 복사하여
백업해놓는것을 **강력하게 추천**

## 1. entity 모듈 생성 & application 모듈 생성
각 모듈별로 .gitignore를 만드는것을 잊지 말자

## 2. 기존 작업내역 application 모듈 으로 이동

- 기존에 존재하던 ```src/main/**``` & ```src/test/**```와  
  ```application/src/main/**``` & ```application/src/test/**```로 이동


- ```build.gradle```의 내용 또한 ```application/build.gradle```로 이동

### 3. application 모듈에 디펜던시 추가

```application/build.gradle```에 엔티티 모듈 및 디펜던시 추가

```groovy
dependencies {
    // codegen 시점에 entity 포함
    jooqGenerator project(':entity')

    // 만약 application 모듈에서 엔티티 모듈을 사용하고 싶다면 주석 해제
    // implementation project(':entity')

    jooqGenerator "org.jooq:jooq-meta-extensions-hibernate:${jooqVersion}"

    // in-memory H2로 JOOQ dsl 생성을 위함
    // h2의 v2 버전 호환성 때문에 상위버전 대신 1.4.200 버전을 추천
    jooqGenerator 'com.h2database:h2:1.4.200'
}
```

### 4. entity 모듈의 build.gradle 설정

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.3'
    id 'io.spring.dependency-management' version '1.1.4'
}

bootJar { enabled = false }
jar { enabled = true }

group = 'org.sight'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation "org.jooq:jooq:${jooqVersion}"

    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
}

test {
    useJUnitPlatform()
}
```

### 5. entity 모듈에 JPA 엔티티 추가
엔티티 모듈안에 ```com.sight.entity``` 패키지의 엔티티들을 참고해서
JPA 엔티티 엔티티 모듈에 추가

### 6. application 모듈의 generateSakilaDBJooq 테스크 실행

```groovy
gradle :application:generateSakilaDBJooq
```