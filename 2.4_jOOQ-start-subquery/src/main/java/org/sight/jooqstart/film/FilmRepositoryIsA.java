package org.sight.jooqstart.film;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JActor;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmActor;
import org.jooq.generated.tables.daos.FilmDao;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.jooq.impl.EnumConverter;
import org.sight.jooqstart.config.converter.PriceCategoryConverter;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Repository
public class FilmRepositoryIsA extends FilmDao {

    private final DSLContext dslContext;
    private final JFilm FILM = JFilm.FILM;

    public FilmRepositoryIsA(Configuration configuration, DSLContext dslContext) {
        super(configuration);
        this.dslContext = dslContext;
    }

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
                        row(FILM.fields()),
                        row(FILM_ACTOR.fields()),
                        row(ACTOR.fields())
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

    /**
     * 1. 스칼라 서브쿼리 (SELECT 절)
     * SELECT
     *     film.film_id,
     *     film.title,
     *     film.rental_rate,
     *     CASE
     *         WHEN rental_rate <= 1.0 THEN 'Cheap'
     *         WHEN rental_rate <= 3.0 THEN 'Moderate'
     *         ELSE 'Expensive'
     *     END AS price_category,
     *     (SELECT COUNT(*) FROM inventory where film_id = film.film_id) AS total_inventory
     * FROM FILM;
     */
    public List<FilmSummary> getFilmSummary() {
        return dslContext.select(
                FILM.FILM_ID,
                FILM.TITLE,
                FILM.RENTAL_RATE,
                case_().when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(1.0)), "Cheap")
                       .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(3.0)), "Moderate")
                       .otherwise("Expensive")
                .as("price_category").convert(new PriceCategoryConverter())
        ).from(FILM)
        .fetchInto(FilmSummary.class);
    }

    /**
     *
     */
}
