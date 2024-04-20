# 섹션 2-3. 조건절 및 동적 조건절 만들기

### 1. AND 절과 OR 절

#### 1.1 AND 절
```java
private final DSLContext dslContext;

Actor fetchActorByFirstNameAndLastName(String firstName, String lastName) {
    return dslContext.selectFrom(ACTOR)
            .where(ACTOR.FIRST_NAME.eq(firstName).and(ACTOR.LAST_NAME.eq(lastName)))
            .fetch();
}

// 또는 
Actor fetchActorByFirstNameAndLastName(String firstName, String lastName) {
    return dslContext.selectFrom(ACTOR)
            .where(
                    ACTOR.FIRST_NAME.eq(firstName),
                    ACTOR.LAST_NAME.eq(lastName)
            ).fetch();
}
```

```mysql
select `actor`.`actor_id`, 
       `actor`.`first_name`,
       `actor`.`last_name`,
       `actor`.`last_update` 
from `actor` 
where (`actor`.`first_name` = ? and `actor`.`last_name` = ?)
```

#### 1.2 OR 절
```java
public List<Actor> fetchActorByFirstNameOrLastName(String firstName, String lastName) {
    return dslContext.selectFrom(ACTOR)
            .where(
                    ACTOR.FIRST_NAME.eq(firstName)
                            .or(ACTOR.LAST_NAME.eq(lastName))
            )
            .fetchInto(Actor.class);
}
```

```mysql
select `actor`.`actor_id`, 
       `actor`.`first_name`,
       `actor`.`last_name`,
       `actor`.`last_update`
from `actor`
where (`actor`.`first_name` = ? or `actor`.`last_name` = ?)
```

### 2. Dynamic Condition 만들기

조건절에서 DSL.noCondition()을 사용하여 조건이 없는 경우 조건절에서 제외할 수 있음.

```java
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public static <T> Condition eqIfNotNull(Field<T> field, T value) {
    if (value == null) {
        return DSL.noCondition();
    }
    return field.eq(value);
}
```

### 3. in 절 동적조건절 만들기
```java
public class JooqListConditionUtils {
    public static <T> Condition inIfNotEmpty(Field<T> field, List<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            return DSL.noCondition();
        }
        return field.in(values);
    }
}
```

```java
@Repository
public class ActorRepository {
    // ... 

    public List<Actor> findByActorIdIn(List<Long> actorIdList) {
        return dslContext.selectFrom(ACTOR)
                .where(inIfNotEmpty(ACTOR.ACTOR_ID, actorIdList))
                .fetchInto(Actor.class);
    }
}
```
