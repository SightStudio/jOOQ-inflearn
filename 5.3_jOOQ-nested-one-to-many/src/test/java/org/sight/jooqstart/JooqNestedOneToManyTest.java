package org.sight.jooqstart;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sight.jooqstart.film.FilmInventorySummary;
import org.sight.jooqstart.film.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class JooqNestedOneToManyTest {

    @Autowired
    FilmRepository filmRepository;

    @Test
    void nestedOneToMany() {
        List<FilmInventorySummary> nestedFilmInventorySummary = filmRepository.findNestedFilmInventorySummary();
        Long countOfAllFilm = filmRepository.countOfAllFilm();

        Assertions.assertThat(Long.valueOf(nestedFilmInventorySummary.size()))
                .isEqualTo(countOfAllFilm);
    }
}
