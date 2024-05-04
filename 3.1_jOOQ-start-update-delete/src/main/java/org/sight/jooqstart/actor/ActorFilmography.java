package org.sight.jooqstart.actor;

import lombok.Getter;
import org.jooq.generated.tables.pojos.Actor;
import org.jooq.generated.tables.pojos.Film;

import java.util.List;

@Getter
public class ActorFilmography {

    private final Actor actor;
    private final List<Film> filmList;

    public ActorFilmography(Actor actor, List<Film> filmList) {
        this.actor = actor;
        this.filmList = filmList;
    }
}
