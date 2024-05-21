# 섹션 5-1. path-based join을 통한 JOIN절 가독성 높이기

- Docs
    - Implicit path join (3.11 ~ )
        - https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/implicit-join/
    - Implicit to-many path join (3.19 ~ )
        - https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/implicit-to-many-join/
    - Explicit path join (3.19 ~ )
        - https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/explicit-path-join/
    - Synthetic foreign keys (상용 라이센스만 가능)
        - https://www.jooq.org/doc/latest/manual/code-generation/codegen-advanced/codegen-config-database/codegen-database-synthetic-objects/codegen-database-synthetic-fks/
        - 외래키가 없더라도 DSL 생성시 설정에서 외래키를 추가 할 수 있음

## NOTICE
해당 프로젝트는 4.2강의 testcontainers + flyway 형태로 작업되어있습니다.

Implicit path join은 가독성이 떨어지고, 실수하기 쉬워 사용을 비추천한다.
특히 **Implicit to-many path join** 는 사용하지 말자.

다만 Explicit path join 외래키가 지원된다면 추천한다.

![schema.png](readme-asset/schema.png)


### 1. FilmRepository 수정

```java
package org.sight.jooqstart.film;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    // ...
    public List<FilmWithActor> findFilmWithActorsListImplicitPathJoin (Long page, Long pageSize) {
        final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        return dslContext.select(
                        DSL.row(FILM.fields()),
                        DSL.row(FILM_ACTOR.fields()),
                        DSL.row(FILM_ACTOR.actor().fields())
                )
                .from(FILM)
                .join(FILM_ACTOR)
                    .on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(FilmWithActor.class);
    }

    public List<FilmWithActor> findFilmWithActorsListExplicitPathJoin (Long page, Long pageSize) {
        return dslContext.select(
                        DSL.row(FILM.fields()),
                        DSL.row(FILM.filmActor().fields()),
                        DSL.row(FILM.filmActor().actor().fields())
                )
                .from(FILM)
                .join(FILM.filmActor())
                .join(FILM.filmActor().actor())
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(FilmWithActor.class);
    }
}
```

### 2. 테스트 코드 작성

```java
@SpringBootTest
public class JooqJoinShortCutTest {

    @Autowired
    FilmRepository filmRepository;

    @Test
    @DisplayName("implicitPathJoin_테스트")
    void implicitPathJoin_테스트() {

        List<FilmWithActor> original = filmRepository.findFilmWithActorsList(1L, 10L);
        List<FilmWithActor> implicit = filmRepository.findFilmWithActorsListImplicitPathJoin(1L, 10L);

        Assertions.assertThat(original)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(implicit);
    }

    @Test
    @DisplayName("explicitPathJoin_테스트")
    void explicitPathJoin_테스트() {

        List<FilmWithActor> original = filmRepository.findFilmWithActorsList(1L, 10L);
        List<FilmWithActor> implicit = filmRepository.findFilmWithActorsListExplicitPathJoin(1L, 10L);

        Assertions.assertThat(original)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(implicit);
    }
}
```

### 3. optional - jOOQ config

```java
@Configuration
public class JooqConfig {
    @Bean
    public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
        return c -> c.settings()
                .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)
                .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)
                .withRenderSchema(false)

                // implicit path join to-many는 기본적으로 에러를 발생시켜 이렇게 수동으로 조인을 지정 해야한다.
                 .withRenderImplicitJoinToManyType(RenderImplicitJoinType.INNER_JOIN)

                // implicit PATH JOIN many-to-one 을 비활성화 하고 싶다면 하고 싶다면
//                 .withRenderImplicitJoinType(RenderImplicitJoinType.THROW)
        ;
    }
}
```