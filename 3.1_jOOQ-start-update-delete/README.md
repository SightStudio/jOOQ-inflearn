# 섹션 3-1. jOOQ로 데이터 수정 / 삭제하기 (update, delete)

- Docs
  - update: https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/update-statement
  - delete: https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/delete-statement/

### 1. update 절
#### 1.1 동적으로 필드 업데이트

```java
@Getter
@Builder
public class ActorUpdateRequest {
    private String firstName;
    private String lastName;
}
```

```java
public int updateWithDto(Long actorId, ActorUpdateRequest request) {
    var firstName = StringUtils.hasText(request.getFirstName()) ? val(request.getFirstName()) : noField(ACTOR.FIRST_NAME);
    var lastName = StringUtils.hasText(request.getLastName()) ? val(request.getLastName()) : noField(ACTOR.LAST_NAME);

    return dslContext.update(ACTOR)
            .set(ACTOR.FIRST_NAME, firstName)
            .set(ACTOR.LAST_NAME, lastName)
            .where(ACTOR.ACTOR_ID.eq(actorId))
            .execute();
}
```

#### 1.1 동적으로 필드 업데이트 (ActiveRecord 사용)
```java
public int updateWithRecord(Long actorId, ActorUpdateRequest request) {
    var record = dslContext.newRecord(ACTOR);

    if (StringUtils.hasText(request.getFirstName())) {
        record.setFirstName(request.getFirstName());
    }

    if (StringUtils.hasText(request.getLastName())) {
        record.setLastName(request.getLastName());
    }

    return dslContext.update(ACTOR)
            .set(record)
            .where(ACTOR.ACTOR_ID.eq(actorId))
            .execute();
    // 또는
    // record.setActorId(actorId);
    // return record.update();
}
```

### 1. delete 절

```java
public int delete(Long actorId) {
    return dslContext.deleteFrom(ACTOR)
            .where(ACTOR.ACTOR_ID.eq(actorId))
            .execute();
}

public int deleteWithActiveRecord(Long actorId) {
    ActorRecord actorRecord = dslContext.newRecord(ACTOR);
    actorRecord.setActorId(actorId);
    return actorRecord.delete();
}
```
  
### WHERE 절이 없는 UPDATE, DELETE 쿼리 실행 차단

```java
@Configuration
public class JooqConfig {
    @Bean
    public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
        return c -> c.settings()
                .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)
                .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)
                .withRenderSchema(false);
    }
}
```