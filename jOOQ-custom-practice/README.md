# 섹션 1-2. jOOQ DSL Custom 하기

### step 1. jOOQ-custom 서브모듈 만들기

![submodule.png](readme_asset%2Fsubmodule.png)

### step 2. build.gradle 설정

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

### step 3. build.gradle 설정

```groovy
jooq {
	version = "${jooqVersion}"
	// ...
    generator {
        name = 'org.jooq.codegen.DefaultGenerator'

        database {
            name = 'org.jooq.meta.mysql.MySQLDatabase'
            unsignedTypes = false
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