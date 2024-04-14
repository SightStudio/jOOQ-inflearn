package org.sight.jooqstart.actor;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.daos.ActorDao;
import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.sight.jooqstart.util.jooq.JooqListConditionUtils.inIfNotEmpty;
import static org.sight.jooqstart.util.jooq.JooqStringConditionUtils.contains;

@Repository
public class ActorRepository {

    private final DSLContext dslContext;
    private final ActorDao actorDao;
    private final JActor ACTOR = JActor.ACTOR;

    public ActorRepository(DSLContext dslContext, Configuration configuration) {
        this.actorDao = new ActorDao(configuration);
        this.dslContext = dslContext;
    }

    public List<Actor> findByActorIdIn(List<Long> actorIdList) {

        if (CollectionUtils.isEmpty(actorIdList)) {
            return Collections.emptyList();
        }

        return dslContext.selectFrom(ACTOR)
                .where(
                        inIfNotEmpty(ACTOR.ACTOR_ID, actorIdList)
                )
                .fetchInto(Actor.class);
    }

    public List<ActorFilmography> findActorFilmographyByFullName(String fullName) {
        final JFilmActor FILM_ACTOR = JFilmActor.FILM_ACTOR;
        final JFilm FILM = JFilm.FILM;

        Map<Actor, List<Film>> actorListMap = dslContext.select(
                    DSL.row(ACTOR.fields()).as("actors"),
                    DSL.row(FILM.fields()).as("films")
                )
                .from(ACTOR)
                .join(FILM_ACTOR)
                    .on(ACTOR.ACTOR_ID.eq(FILM_ACTOR.ACTOR_ID))
                .join(FILM)
                    .on(FILM_ACTOR.FILM_ID.eq(FILM.FILM_ID))
                .where(
                    contains(ACTOR.FIRST_NAME.concat(" ").concat(ACTOR.LAST_NAME), fullName)
                )
                .fetchGroups(
                        record -> record.get("actors", Actor.class),
                        record -> record.get("films", Film.class)
                );

        return actorListMap.entrySet().stream()
                .map(entry -> new ActorFilmography(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
