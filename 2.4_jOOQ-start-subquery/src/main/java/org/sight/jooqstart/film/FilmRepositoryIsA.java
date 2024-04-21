package org.sight.jooqstart.film;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.generated.tables.*;
import org.jooq.generated.tables.daos.FilmDao;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.sight.jooqstart.config.converter.PriceCategoryConverter;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static org.jooq.impl.DSL.*;
import static org.sight.jooqstart.util.jooq.JooqStringConditionUtils.containsIfNotBlank;

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
     * film.film_id,
     * film.title,
     * film.rental_rate,
     * CASE
     * WHEN rental_rate <= 1.0 THEN 'Cheap'
     * WHEN rental_rate <= 3.0 THEN 'Moderate'
     * ELSE 'Expensive'
     * END AS price_category,
     * (SELECT COUNT(*) FROM inventory where film_id = film.film_id) AS total_inventory
     * FROM FILM;
     */
    public List<FilmPriceSummary> findFilmPriceSummaryByFilmTitleLike(String filmTitle) {
        return dslContext.select(
                        FILM.FILM_ID,
                        FILM.TITLE,
                        FILM.RENTAL_RATE,
                        case_().when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(1.0)), "Cheap")
                                .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(3.0)), "Moderate")
                                .otherwise("Expensive")
                                .as("price_category").convert(new PriceCategoryConverter())
                ).from(FILM)
                .where(containsIfNotBlank(FILM.TITLE, filmTitle))
                .fetchInto(FilmPriceSummary.class);
    }

    /**
     * SELECT film.film_id,
     * film.title,
     * rental_duration_info.average_rental_duration
     * FROM film
     * JOIN
     * (SELECT inventory.film_id, AVG(DATEDIFF(rental.return_date, rental.rental_date)) AS average_rental_duration
     * FROM rental JOIN
     * inventory ON rental.inventory_id = inventory.inventory_id
     * WHERE rental.return_date IS NOT NULL
     * GROUP BY inventory.film_id
     * ) AS rental_duration_info
     * ON film.film_id = rental_duration_info.film_id
     * ORDER BY rental_duration_info.average_rental_duration DESC;
     */
    public List<FilmRentalSummary> findFilmRentalSummaryByFilmTitleLike(String filmTitle) {
        JInventory INVENTORY = JInventory.INVENTORY;
        JRental RENTAL = JRental.RENTAL;

        var rentalDurationInfoSubquery = select(INVENTORY.FILM_ID,
                avg(DSL.localDateTimeDiff(DatePart.DAY, RENTAL.RENTAL_DATE, RENTAL.RETURN_DATE)).as("average_rental_duration"))
                .from(RENTAL)
                .join(INVENTORY)
                .on(RENTAL.INVENTORY_ID.eq(INVENTORY.INVENTORY_ID))
                .where(RENTAL.RETURN_DATE.isNotNull())
                .groupBy(INVENTORY.FILM_ID)
                .asTable("rental_duration_info");

        return dslContext.select(FILM.FILM_ID, FILM.TITLE, rentalDurationInfoSubquery.field("average_rental_duration"))
                .from(FILM)
                .join(rentalDurationInfoSubquery)
                .on(FILM.FILM_ID.eq(rentalDurationInfoSubquery.field(INVENTORY.FILM_ID)))
                .where(containsIfNotBlank(FILM.TITLE, filmTitle))
                .orderBy(field(name("average_rental_duration")).desc())
                .fetchInto(FilmRentalSummary.class);
    }

    public List<Film> findRentedFilmByTitle(String filmTitle) {
        JInventory INVENTORY = JInventory.INVENTORY;
        JRental RENTAL = JRental.RENTAL;

        return dslContext.selectFrom(FILM)
                .whereExists(selectOne()
                        .from(INVENTORY)
                        .join(RENTAL).on(INVENTORY.INVENTORY_ID.eq(RENTAL.INVENTORY_ID))
                        .where(INVENTORY.FILM_ID.eq(FILM.FILM_ID))
                ).and(containsIfNotBlank(FILM.TITLE, filmTitle))
                .fetchInto(Film.class);
    }
}
