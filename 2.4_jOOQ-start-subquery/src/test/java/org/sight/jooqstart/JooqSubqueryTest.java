package org.sight.jooqstart;

import org.assertj.core.api.Assertions;
import org.jooq.generated.tables.pojos.Actor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sight.jooqstart.actor.ActorFilmography;
import org.sight.jooqstart.actor.ActorFilmographySearchOption;
import org.sight.jooqstart.actor.ActorRepository;
import org.sight.jooqstart.film.FilmRepositoryIsA;
import org.sight.jooqstart.film.FilmSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class JooqSubqueryTest {

    @Autowired
    private FilmRepositoryIsA filmRepository;

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름으로 조회")
    void test() {
        List<FilmSummary> filmSummary = filmRepository.getFilmSummary();
        System.out.println(filmSummary);
    }

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름과 영화 제목으로 조회")
    void test2() {

    }

    @Test
    @DisplayName("")
    void test3() {

    }
}
