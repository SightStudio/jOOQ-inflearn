# 질문 1314279 
## jOOQ Kotlin Generator로 생성된 POJO 클래스의 nullable 설정  

질문명: db column은 not null로 되어 있는데 kotlin에서 pojo가 다 nullable로 생성되요.  
링크: https://www.inflearn.com/questions/1314279

# 관련 jOOQ 문서
https://www.jooq.org/doc/latest/manual/code-generation/kotlingenerator/

build.gradle
```groovy
tasks {
    generateJooqClasses {
        schemas.set(["sakila"])
        outputDirectory.set(project.layout.projectDirectory.dir("src/generated"))
        includeFlywayTable.set(false)

        usingJavaConfig {
            // KotlinGenerator 사용
            name = 'org.jooq.codegen.KotlinGenerator'
            generate = new Generate()
                    .withJavaTimeTypes(true)
                    .withDeprecated(false)
                    .withDaos(true)
                    .withFluentSetters(true)
                    .withRecords(true)
            
                     // kotlin - pojo notnull 설정 (default - false)
                    .withKotlinNotNullPojoAttributes(true)

                    // kotlin - ActiveRecord notnull 설정 (default - false)
                    .withKotlinNotNullRecordAttributes(true)

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
