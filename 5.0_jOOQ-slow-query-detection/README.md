# 섹션 5.0 ExecuteListener를 통해 application에서 슬로우 쿼리 감지하기

- Docs
  - ExecuteListeners: https://www.jooq.org/doc/latest/manual/sql-execution/execute-listeners/

### 1. PerformanceListener 생성

```java
public class PerformanceListener implements ExecuteListener {

    private StopWatch watch;
    private static final Duration SLOW_QUERY_LIMIT = Duration.ofSeconds(3);

    @Override
    public void executeStart(ExecuteContext ctx) {
        watch = new StopWatch();
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        final long queryTimeNano = watch.split();

        if (queryTimeNano > SLOW_QUERY_LIMIT.toNanos()) {
            Query query = ctx.query();
            Duration executeTime = Duration.ofNanos(queryTimeNano);
            log.warn(
                    String.format(
                            """
                            ### Slow SQL 탐지 >>
                            경고: jOOQ로 실행된 쿼리 중 %d초 이상 실행된 쿼리가 있습니다.
                            실행시간: %s초
                            실행쿼리: %s
                            """
                            , SLOW_QUERY_LIMIT.toSeconds()
                            , millisToSeconds(executeTime)
                            , query
                    )
            );
        }
    }

    private String millisToSeconds(Duration duration) {
        return String.format("%.1f", duration.toMillis() / 1000.0);
    }
}
```

### 2. jOOQ config에 PerformanceListener 등록
Spring Boot 3.x 기준

```java
@Configuration
public class JooqConfig {
    @Bean
    public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
        return c -> {
            c.set(PerformanceListener::new);
            c.settings()
                    .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)
                    .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)
                    .withRenderSchema(false);
        };
    }
}
```

### 3. 슬로우 쿼리 실행 후 로그 확인
```java
import static org.jooq.impl.DSL.*;

@SpringBootTest
public class JooqSlowQueryTest {

    @Autowired
    DSLContext dslContext;

    @Test
    @DisplayName("SLOW 쿼리 탐지테스트")
    void 슬로우쿼리_탐지_테스트() {
        dslContext.select(DSL.field("SLEEP(4)"))
                .from(dual())
                .execute();
    }
}

```