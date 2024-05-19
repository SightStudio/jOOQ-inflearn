# 섹션 4-1. JPA 엔티티를 기반으로 DSL 만들기

- Docs
    - JPADatabase: Code generation from entities
        - https://www.jooq.org/doc/latest/manual/code-generation/codegen-jpa

### 0. 필독

해당 내역을 동일하게 작업하기 전에 이전에 작업한 내용들을 복사하여
백업해놓는것을 **강력하게 추천**

### 1. entity 모듈 생성
.gitignore 를 만드는것을 잊지 말자

### 2. entity 모듈의 build.gradle 설정

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

### 3. entity 모듈에 JPA 엔티티 추가
엔티티 모듈안에 ```com.sight.entity``` 패키지의 엔티티들을 참고해서
JPA 엔티티 엔티티 모듈에 추가

### 4. root의 build.gradle에 의존성 추가

```groovy
dependency {
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

### 5. root build.gradle의 jOOQ database를 JPA database로 변경
build.gradle
```groovy

jooq {
    version = "${jooqVersion}"
    configurations {
        sakilaDB {
            generationTool {
                generator {
                    name = 'org.jooq.codegen.DefaultGenerator'

                    database {
                        name = 'org.jooq.meta.extensions.jpa.JPADatabase'
                        unsignedTypes = true

                        properties {
                            property {
                                key = 'packages'
                                value = 'com.sight.entity'
                            }

                            // DSL 생성시, jpa의 AttributeConverters 에 따라 jooq DSL의 타입을 매핑할지 여부
                            property {
                                key = 'useAttributeConverters'
                                value = true
                            }
                        }

                        forcedTypes {
                            forcedType {
                                userType = 'java.lang.Long'
                                includeTypes = 'int unsigned'
                            }

                            forcedType {
                                userType = 'java.lang.Integer'
                                includeTypes = 'tinyint unsigned'
                            }

                            forcedType {
                                userType = 'java.lang.Integer'
                                includeTypes = 'smallint unsigned'
                            }
                        }
                    }

                    generate {
                        daos = true
                        records = true
                        fluentSetters = true
                        javaTimeTypes = true
                        deprecated = false
                    }

                    target {
                        directory = 'src/generated'
                    }

                    // jooq-custom 내부의 설정
                    strategy.name = 'jooq.custom.generator.JPrefixGeneratorStrategy'
                }
            }
        }
    }
}
```

### 6. application 모듈의 generateSakilaDBJooq 테스크 실행

```groovy
gradle :application:generateSakilaDBJooq
```

