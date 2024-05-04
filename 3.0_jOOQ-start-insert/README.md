# 섹션 3-0. jOOQ로 데이터 생성하기 insert

### 1. 자동생성된 DAO를 통한 insert

```java
@Repository
public class ActorRepository {
    public Actor saveByDao(Actor actor) {
        // 이때 PK (actorId)가 actor 객체에 추가됨
        actorDao.insert(actor);
        return actor;
    }   
}
```

### 2. ActiveRecord 를 통한 insert
```java
@Repository
public class ActorRepository {
    public ActorRecord saveByRecord(Actor actor) {
        ActorRecord actorRecord = dslContext.newRecord(ACTOR, actor);
        actorRecord.insert();
        return actorRecord;
    }
}
```

### 3. SQL 실행 후 PK만 반환
```java
@Repository
public class ActorRepository {
    public Long saveWithReturningPkOnly(Actor actor) {
        return dslContext.insertInto(ACTOR,
                        ACTOR.FIRST_NAME,
                        ACTOR.LAST_NAME
                )
                .values(
                        actor.getFirstName(),
                        actor.getLastName()
                )
                .returningResult(ACTOR.ACTOR_ID)
                .fetchOneInto(Long.class);
    }
}
```

### 4. SQL 실행 후 해당 ROW 전체 반환
```java
@Repository
public class ActorRepository {
    public Actor saveWithReturning(Actor actor) {
        return dslContext.insertInto(ACTOR,
                        ACTOR.FIRST_NAME,
                        ACTOR.LAST_NAME
                )
                .values(
                        actor.getFirstName(),
                        actor.getLastName()
                )
                .returning(ACTOR.fields())
                .fetchOneInto(Actor.class);
    }
}
```

### 5. Bulk Insert
```java
@Repository
public class ActorRepository {
    public void bulkInsertWithRows(List<Actor> actorList) {
        var rows = actorList.stream()
                .map(actor -> DSL.row(
                        actor.getFirstName(),
                        actor.getLastName()
                )).toList();

        dslContext.insertInto(ACTOR,
                        ACTOR.FIRST_NAME, ACTOR.LAST_NAME
                ).valuesOfRows(rows)
                .execute();
    }
}
```