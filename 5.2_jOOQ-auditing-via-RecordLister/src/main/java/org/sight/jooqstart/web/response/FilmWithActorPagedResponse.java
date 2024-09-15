package org.sight.jooqstart.web.response;

import lombok.Getter;
import org.sight.jooqstart.film.FilmWithActor;

import java.util.List;

@Getter
public class FilmWithActorPagedResponse {

    private final PagedResponse page;
    private final List<FilmActorResponse> filmActor;

    public FilmWithActorPagedResponse(PagedResponse page, List<FilmWithActor> filmWithActors) {
        this.page = page;
        this.filmActor = filmWithActors.stream()
                .map(FilmActorResponse::new)
                .toList();
    }

    @Getter
    public static class FilmActorResponse {

        private final String filmTitle;
        private final int filmLength;
        private final String actorFullName;

        public FilmActorResponse(FilmWithActor filmWithActor) {
            this.filmTitle = filmWithActor.getFilm().getTitle();
            this.filmLength = filmWithActor.getFilm().getLength();
            this.actorFullName = filmWithActor.getFullActorName();
        }
    }
}
