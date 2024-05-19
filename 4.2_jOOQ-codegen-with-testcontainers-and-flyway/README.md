# 섹션 4-2. Testcontainers + Flyway를 통해 DDL로 jOOQ DSL 만들기

gradle 플러그인
- 여태까지 우리가 사용했던것
  - https://github.com/etiennestuder/gradle-jooq-plugin
- 이번에 우리가 사용할 것
  - https://github.com/monosoul/jooq-gradle-plugin
### 1. jOOQ plugin 교체

nu.studer.jooq -> dev.monosoul.jooq-docker로 교체

### 2. build.gradle 수정
```groovy
dependencies {
    // ...
    jooqCodegen project(':jooq-custom')
    jooqCodegen "org.jooq:jooq:${jooqVersion}"
    jooqCodegen "org.jooq:jooq-meta:${jooqVersion}"
    jooqCodegen "org.jooq:jooq-codegen:${jooqVersion}"
    
    jooqCodegen 'org.flywaydb:flyway-core:10.8.1'
    jooqCodegen 'org.flywaydb:flyway-mysql:10.8.1'
}
```

```groovy
import org.jooq.meta.jaxb.*

jooq {
    version = "${jooqVersion}"
    withContainer {
        image {
            name = "mysql:8.0.29"
            envVars = [
                MYSQL_ROOT_PASSWORD: "passwd",
                MYSQL_DATABASE     : "sakila"
            ]
        }

        db {
            username = "root"
            password = "passwd"
            name = "sakila"
            port = 3306
            jdbc {
                schema = "jdbc:mysql"
                driverClassName = "com.mysql.cj.jdbc.Driver"
            }
        }
    }
}

tasks {
    generateJooqClasses {
        schemas.set(["sakila"])
        outputDirectory.set(project.layout.projectDirectory.dir("src/generated"))
        includeFlywayTable.set(false)

        usingJavaConfig {
            generate = new Generate()
                    .withJavaTimeTypes(true)
                    .withDeprecated(false)
                    .withDaos(true)
                    .withFluentSetters(true)
                    .withRecords(true)

            withStrategy(
                    new Strategy().withName("jooq.custom.generator.JPrefixGeneratorStrategy")
            )

            database.withForcedTypes(
                    new ForcedType()
                            .withUserType("java.lang.Long")
                            .withTypes("int unsigned"),
                    new ForcedType()
                            .withUserType("java.lang.Integer")
                            .withTypes("tinyint unsigned"),
                    new ForcedType()
                            .withUserType("java.lang.Integer")
                            .withTypes("smallint unsigned")
            )
        }
    }
}
```

### 3. src/db/migration 폴더에 flyway 파일 추가
flyway에 사용될 파일은 ```V{버전 번호}__{name}.sql``` 형식이 되어야한다.
V1__init_tables.sql 이라는 파일로 추가

### 4. gradle -> jooq > generateJooqClasses 테스크 실행
