package org.sight.jooqstart.actor;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.daos.ActorDao;
import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.generated.tables.records.ActorRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.*;
import static org.sight.jooqstart.util.jooq.JooqListConditionUtils.inIfNotEmpty;
import static org.sight.jooqstart.util.jooq.JooqStringConditionUtils.containsIfNotBlank;

@Repository
public class ActorRepository {

    private final DSLContext dslContext;
    private final ActorDao actorDao;
    private final JActor ACTOR = JActor.ACTOR;

    public ActorRepository(DSLContext dslContext, Configuration configuration) {
        this.actorDao = new ActorDao(configuration);
        this.dslContext = dslContext;
    }

    public List<Actor> findByFirstnameAndLastName(String firstName, String lastName) {
        return dslContext.selectFrom(ACTOR)
                .where(
                        ACTOR.FIRST_NAME.eq(firstName),
                        ACTOR.LAST_NAME.eq(lastName)
                ).fetchInto(Actor.class);
    }

    public List<Actor> findByFirstnameOrLastName(String firstName, String lastName) {
        return dslContext.selectFrom(ACTOR)
                .where(
                        ACTOR.FIRST_NAME.eq(firstName).or(ACTOR.LAST_NAME.eq(lastName)),
                        ACTOR.FIRST_NAME.eq(firstName)
                ).fetchInto(Actor.class);
    }

    public List<Actor> findByActorIdIn(List<Long> idList) {
        return dslContext.selectFrom(ACTOR)
                .where(inIfNotEmpty(ACTOR.ACTOR_ID, idList))
                .fetchInto(Actor.class);
    }

    public List<ActorFilmography> findActorFilmography(ActorFilmographySearchOption searchOption) {
        final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        final JFilm FILM = JFilm.FILM;

        Map<Actor, List<Film>> actorListMap = dslContext.select(
                        row(ACTOR.fields()).as("actor"),
                        row(FILM.fields()).as("film")
                ).from(ACTOR)
                .join(FILM_ACTOR)
                .on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
                .join(FILM)
                .on(FILM.FILM_ID.eq(FILM_ACTOR.FILM_ID))
                .where(
                        // 배우 full name like 검색
                        containsIfNotBlank(ACTOR.FIRST_NAME.concat(" ").concat(ACTOR.LAST_NAME), searchOption.getActorName()),

                        // 영화 제목 like 검색
                        containsIfNotBlank(FILM.TITLE, searchOption.getFilmTitle())
                )
                .fetchGroups(
                        record -> record.get("actor", Actor.class),
                        record -> record.get("film", Film.class)
                );

        return actorListMap.entrySet().stream()
                .map(entry -> new ActorFilmography(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * 이 부분이 지원되기까지 굉장히 많은 논의가 있었음
     * jOOQ 3.19 부터 지원
     * 
     * 참고) https://github.com/jOOQ/jOOQ/issues/2536
     * @return insert 시에 생성된 PK 값이 세팅된 pojo
     */
    public Actor saveByDao(Actor actor) {
        // 이때 PK (actorId)가 actor 객체에 추가됨
        actorDao.insert(actor);
        return actor;
    }

    public ActorRecord saveByRecord(Actor actor) {
        ActorRecord actorRecord = dslContext.newRecord(ACTOR, actor);
        actorRecord.insert();
        
        // 다만 이 방식은 immutable pojo 에서 사용하기 어려울 수 있음
        // actor.setActorId(actorRecord.getActorId());
        return actorRecord;
    }

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

    @Transactional
    public void bulkInsertWithRows(List<Actor> actorList) {
        var rows = actorList.stream()
                .map(actor -> row(
                        actor.getFirstName(),
                        actor.getLastName()
                )).toList();

        dslContext.insertInto(ACTOR,
                ACTOR.FIRST_NAME, ACTOR.LAST_NAME
        ).valuesOfRows(rows)
        .execute();
    }

    public void update(Actor actor) {
        actorDao.update(actor);
    }

    public Actor findByActorId(Long actorId) {
        return actorDao.findById(actorId);
    }

    public int updateWithDto(Long actorId, ActorUpdateRequest request) {
        var firstName = StringUtils.hasText(request.getFirstName()) ? val(request.getFirstName()) : noField(ACTOR.FIRST_NAME);
        var lastName = StringUtils.hasText(request.getLastName()) ? val(request.getLastName()) : noField(ACTOR.LAST_NAME);

        return dslContext.update(ACTOR)
                .set(ACTOR.FIRST_NAME, firstName)
                .set(ACTOR.LAST_NAME, lastName)
                .where(ACTOR.ACTOR_ID.eq(actorId))
                .execute();
    }

    public int updateWithRecord(Long actorId, ActorUpdateRequest request) {
        var record = dslContext.fetchOne(ACTOR, ACTOR.ACTOR_ID.eq(actorId));

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

    public int delete(Long actorId) {
        return dslContext.deleteFrom(ACTOR)
                .where(ACTOR.ACTOR_ID.eq(actorId))
                .execute();
    }

    public int deleteWithRecord(Long actorId) {
        var actorRecord = dslContext.fetchOne(ACTOR, ACTOR.ACTOR_ID.eq(actorId));
        return actorRecord.delete();
    }

    public ActorRecord findRecordByActorId(Long actorId) {
        return dslContext.fetchOne(ACTOR, ACTOR.ACTOR_ID.eq(actorId));
    }
}
