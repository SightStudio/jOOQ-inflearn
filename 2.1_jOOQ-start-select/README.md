# 섹션 1-2. jOOQ DSL Custom 하기

### step 1. jOOQ-custom 서브모듈 만들기

![submodule.png](readme_asset%2Fsubmodule.png)

### step 2. build.gradle 설정

```jOOQ-custom > build.gradle```
```java
dependencies {
    implementation "org.jooq:jooq-codegen:${jooqVersion}"
    runtimeOnly 'com.mysql:mysql-connector-j:8.2.0'
}
```

```java/jooq/custom/generator/JPrefixGeneratorStrategy.java```
```java
public class JPrefixGeneratorStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        if (mode == Mode.DEFAULT) {
            return "J" + super.getJavaClassName(definition, mode);
        }
        return super.getJavaClassName(definition, mode);
    }
}
```

gradle dependency 추가
```groovy
dependencies {
    jooqGenerator project(':jOOQ-custom')
}
```

### step 3. build.gradle 설정

```groovy
jooq {
	version = "${jooqVersion}"
	// ...
    generator {
        name = 'org.jooq.codegen.DefaultGenerator'

        database {
            name = 'org.jooq.meta.mysql.MySQLDatabase'
            unsignedTypes = true
            schemata {
                schema {
                    inputSchema = 'sakila'
                }
            }
        }

        generate {
            daos = true
            records = true
            fluentSetters = true
            javaTimeTypes = true
            deprecated = false


            // jpaAnnotations = true
            // jpaVersion = 2.2
            // validationAnnotations = true
            // springAnnotations = true
            // springDao = true
        }

        // jooq-custom 내부의 설정
        strategy.name = 'jooq.custom.generator.JPrefixGeneratorStrategy'
    }
	// ...
}
```

### step 4. jOOQ runtime configuration 설정

application.properties
```properties
logging.level.org.jooq.tools.LoggerListener=DEBUG
```

Spring Boot 3.x ~
```java
@Configuration
public class JooqConfig {
    @Bean
    public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
        return c -> c.settings().withRenderSchema(false);
    }
}
```

~ Spring Boot 2.x
```java
@Configuration
public class JooqConfig {

    @Bean
    Settings jooqSettings() {
        return new Settings()
                .withRenderSchema(false);
    }
}

```