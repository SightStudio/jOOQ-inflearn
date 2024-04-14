package org.sight.jooqstart.category;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JCategory;
import org.jooq.generated.tables.JFilm;
import org.jooq.generated.tables.JFilmCategory;
import org.jooq.generated.tables.daos.CategoryDao;
import org.jooq.generated.tables.pojos.Category;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.impl.DSL;
import org.sight.jooqstart.actor.ActorFilmography;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.sight.jooqstart.util.jooq.JooqListConditionUtils.inIfNotEmpty;
import static org.sight.jooqstart.util.jooq.JooqStringConditionUtils.contains;

@Repository
public class CategoryRepository {

    private final DSLContext dslContext;
    private final CategoryDao categoryDao;
    private final JCategory CATEGORY = JCategory.CATEGORY;

    public CategoryRepository(DSLContext dslContext, Configuration configuration) {
        this.categoryDao = new CategoryDao(configuration);
        this.dslContext = dslContext;
    }

    public List<Category> findByIdIn(List<Long> categoryIdList) {
        if (CollectionUtils.isEmpty(categoryIdList)) {
            return Collections.emptyList();
        }

        return dslContext.selectFrom(CATEGORY)
                .where(
                        inIfNotEmpty(CATEGORY.CATEGORY_ID, categoryIdList)
                )
                .fetchInto(Category.class);
    }

    public List<CategoryWithFilms> findCategoryWithFilmsByName(String name) {
        var FILM = JFilm.FILM;
        var FILM_CATEGORY = JFilmCategory.FILM_CATEGORY;

        Map<Category, List<Film>> categoryListMap = dslContext.select(
                        DSL.row(CATEGORY.fields()).as("category"),
                        DSL.row(FILM.fields()).as("film")
                )
                .from(CATEGORY)
                .join(FILM_CATEGORY)
                .on(CATEGORY.CATEGORY_ID.eq(FILM_CATEGORY.CATEGORY_ID))
                .join(FILM)
                .on(FILM_CATEGORY.FILM_ID.eq(FILM.FILM_ID))
                .where(
                        contains(CATEGORY.NAME, name)
                )
                .fetchGroups(
                        record -> record.get("category", Category.class),
                        record -> record.get("film", Film.class)
                );

        return categoryListMap.entrySet().stream()
                .map(entry -> new CategoryWithFilms(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
