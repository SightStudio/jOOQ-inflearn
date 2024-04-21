# 섹션 2-3. 조건절 및 동적 조건절 만들기

### 0. 조건절

jOOQ에서 where, having 절 안에 들어가는 조건은  
모두 ```org.jooq.Condition``` 를 상속받는 구현체이다.  
조건절에 사용되는 Condition 들은 모두 메서드 체이닝 형태로 사용할 수 있다.

### 1. AND 절과 OR 절

#### 1.1 AND 절
- and 조건 검색 - fistName와 LastName이 일치하는 배우 조회

```java
@Repository
@RequiredArgsConstructor
public class ActorRepository {
    
    private final DSLContext dslContext;
    
    // ...
    
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

- or 조건 검색 - fistName 또는 LastName이 일치하는 배우 조회

```java
@Repository
@RequiredArgsConstructor
public class ActorRepository {

    private final DSLContext dslContext;
    
    // ...
    public List<Actor> fetchActorByFirstNameOrLastName(String firstName, String lastName) {
        return dslContext.selectFrom(ACTOR)
                .where(
                        ACTOR.FIRST_NAME.eq(firstName)
                                .or(ACTOR.LAST_NAME.eq(lastName))
                )
                .fetchInto(Actor.class);[README.md](README.md)
    }
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

public class JooqConditionUtils {
    public static <T> Condition eqIfNotNull(Field<T> field, T value) {
        if (value == null) {
            return DSL.noCondition();
        }
        return field.eq(value);
    }
}
```

### 3. in 절 동적조건절 만들기

- in절 - 동적 조건 검색

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

    private final DSLContext dslContext;

    // ...
    public List<Actor> findByActorIdIn(List<Long> actorIdList) {
        return dslContext.selectFrom(ACTOR)
                .where(inIfNotEmpty(ACTOR.ACTOR_ID, actorIdList))
                .fetchInto(Actor.class);
    }
}
```
## 4. 

- 다중 조건 검색

```java
@Builder
@Getter
public class ActorFilmographySearchOption {

    // 배우 이름
    private final String actorName;

    // 영화 제목
    private final String filmTitle;
}
```

```java
@Repository
public class ActorRepository {

    private final DSLContext dslContext;
    
    // ...

    public List<ActorFilmography> findActorFilmographyByFullName(ActorFilmographySearchOption searchOption) {
        final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        final JFilm FILM = JFilm.FILM;

        Map<Actor, List<Film>> actorListMap = dslContext.select(
                        DSL.row(ACTOR.fields()).as("actor"),
                        DSL.row(FILM.fields()).as("films")
                )
                .from(ACTOR)
                .join(FILM_ACTOR)
                .on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
                .join(FILM)
                .on(FILM_ACTOR.FILM_ID.eq(FILM.FILM_ID))
                .where(
                        // 배우 full name like 검색
                        containsIfNotBlank(ACTOR.FIRST_NAME.concat(" ").concat(ACTOR.LAST_NAME), searchOption.getActorName()),

                        // 영화 제목 like 검색
                        containsIfNotBlank(FILM.TITLE, searchOption.getFilmTitle())
                )
                .fetchGroups(
                        record -> record.get("actor", Actor.class),
                        record -> record.get("films", Film.class)
                );

        return actorListMap.entrySet().stream()
                .map(entry -> new ActorFilmography(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
```