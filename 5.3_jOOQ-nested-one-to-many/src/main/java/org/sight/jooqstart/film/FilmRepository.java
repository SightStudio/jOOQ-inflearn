package org.sight.jooqstart.film;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.tables.*;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<FilmWithActor> findFilmWithActorsList(Long page, Long pageSize) {
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
                .fetchInto(FilmWithActor.class);
    }

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

    public List<FilmInventorySummary> findNestedFilmInventorySummary() {
        final JFilm FILM = JFilm.FILM;

        final List<FilmInventoryRental> filmInventoryRentals = dslContext.select(
                        DSL.row(FILM.fields()),
                        DSL.row(FILM.inventory().fields()),
                        DSL.row(FILM.inventory().rental().fields())
                )
                .from(FILM)
                .leftJoin(FILM.inventory())
                .leftJoin(FILM.inventory().rental())
                .fetchInto(FilmInventoryRental.class);

        return filmInventoryRentals.stream()
                .collect(
                        Collectors.groupingBy(
                                FilmInventoryRental::getFilm,
                                Collectors.groupingBy(
                                        FilmInventoryRental::getInventory,
                                        Collectors.mapping(FilmInventoryRental::getRental, Collectors.toList())
                                )
                        )
                ).entrySet()
                .stream()
                .map(FilmInventorySummary::new)
                .sorted(Comparator.comparingLong(FilmInventorySummary::getFilmId))
                .toList();
    }

    public Long countOfAllFilm() {
        return dslContext.selectCount()
                .from(FILM)
                .fetchOneInto(Long.class);
    }
}
