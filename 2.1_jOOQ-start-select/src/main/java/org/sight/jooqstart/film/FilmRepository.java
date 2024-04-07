package org.sight.jooqstart.film;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FilmRepository {

    private final DSLContext dslContext;

    public Result<Record> findFilmWithActor() {
        return dslContext.select()
                .from("film")
                .join("film_actor")
                .on("film.film_id = film_actor.film_id")
                .fetch();
    }
}
