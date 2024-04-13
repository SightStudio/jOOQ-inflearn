# 섹션 2-1. jOOQ 기본 시작하기

### step 1. unsigned 타입 변환

실습 편의를 위해 
**forcedTypes** unsigned 타입을 자바의 기본 타입으로 매핑한다.

```groovy
forcedTypes {
    forcedType {
        userType = 'java.lang.Long'
        includeTypes = 'int unsigned'
    }

    forcedType {
        userType = 'java.lang.Integer'
        includeTypes = 'tinyint unsigned'
    }

    forcedType {
        userType = 'java.lang.Integer'
        includeTypes = 'smallint unsigned'
    }
}
```

### step 2. 도메인 객체 및 DTO 생성

도메인 모델
[FilmWithActors.java](src%2Fmain%2Fjava%2Forg%2Fsight%2Fjooqstart%2Ffilm%2FFilmWithActors.java)  
[SimpleFilmInfo.java](src%2Fmain%2Fjava%2Forg%2Fsight%2Fjooqstart%2Ffilm%2FSimpleFilmInfo.java)

[FilmWithActorPagedResponse.java](src%2Fmain%2Fjava%2Forg%2Fsight%2Fjooqstart%2Fweb%2Fresponse%2FFilmWithActorPagedResponse.java)  
[PagedResponse.java](src%2Fmain%2Fjava%2Forg%2Fsight%2Fjooqstart%2Fweb%2Fresponse%2FPagedResponse.java)

### step 3. FilmRepository 및 FilmService 작성

```java
@Repository
@RequiredArgsConstructor
public class FilmRepository {

    private final DSLContext dslContext;
    private final JFilm FILM = JFilm.FILM;

    public Film findById(Long id) {
        return dslContext.select(FILM.fields())
                .from(FILM)
                .where(FILM.FILM_ID.eq(id))
                .fetchOneInto(Film.class);
    }

    public SimpleFilmInfo findByIdAsSimpleFilmInfo(Long id) {
        return dslContext.select(FILM.FILM_ID, FILM.TITLE, FILM.DESCRIPTION)
                .from(FILM)
                .where(FILM.FILM_ID.eq(id))
                .fetchOneInto(SimpleFilmInfo.class);
    }

    public List<FilmWithActors> findFilmWithActorsList(Long page, Long pageSize) {
        final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        final JActor ACTOR = JActor.ACTOR;
        return dslContext.select(
                    DSL.row(FILM.fields()),
                    DSL.row(FILM_ACTOR.fields()),
                    DSL.row(ACTOR.fields())
                )
                .from(FILM)
                .join(FILM_ACTOR)
                    .on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
                .join(ACTOR)
                    .on(FILM_ACTOR.ACTOR_ID.eq(ACTOR.ACTOR_ID))
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(FilmWithActors.class);
    }
}

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmWithActorPagedResponse getFilmActorPageResponse (Long page, Long pageSize) {
        List<FilmWithActors> filmWithActorsList = filmRepository.findFilmWithActorsList(page, pageSize);
        return new FilmWithActorPagedResponse(
                new PagedResponse(page, pageSize),
                filmWithActorsList
        );
    }

    public SimpleFilmInfo getSimpleFilmInfo (Long filmId) {
        return filmRepository.findByIdAsSimpleFilmInfo(filmId);
    }
}
```

### step 4. 테스트 코드 작성

```java
@Test
@DisplayName("1) 영화정보 조회")
void test() {
    Film film = filmRepository.findById(1L);
    Assertions.assertThat(film).isNotNull();
}

@Test
@DisplayName("2) 영화정보 간략 조회")
void test2() {
    SimpleFilmInfo simpleFilmInfo = filmService.getSimpleFilmInfo(1L);
    Assertions.assertThat(simpleFilmInfo).hasNoNullFieldsOrProperties();
}

@Test
@DisplayName("3) 영화와 배우 정보를 페이징하여 조회. - 응답까지")
void test3() {
    FilmWithActorPagedResponse filmActorPageResponse = filmService.getFilmActorPageResponse(1L, 10L);
    Assertions.assertThat(filmActorPageResponse)
            .extracting("filmActor").asList().isNotEmpty();
}
```
