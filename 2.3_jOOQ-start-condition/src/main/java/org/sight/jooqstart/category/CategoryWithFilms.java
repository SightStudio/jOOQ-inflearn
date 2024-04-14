package org.sight.jooqstart.category;

import lombok.Getter;
import org.jooq.generated.tables.pojos.Category;
import org.jooq.generated.tables.pojos.Film;

import java.util.List;

@Getter
public class CategoryWithFilms {

    private final Category category;
    private final List<Film> filmList;

    public CategoryWithFilms(Category category, List<Film> filmList) {
        this.category = category;
        this.filmList = filmList;
    }
}
