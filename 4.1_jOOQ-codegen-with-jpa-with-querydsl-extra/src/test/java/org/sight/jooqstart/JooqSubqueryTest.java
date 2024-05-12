package org.sight.jooqstart;

import org.jooq.generated.tables.pojos.Film;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sight.jooqstart.film.FilmPriceSummary;
import org.sight.jooqstart.film.FilmRentalSummary;
import org.sight.jooqstart.film.FilmRepositoryIsA;
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
        List<FilmPriceSummary> filmSummary = filmRepository.findFilmPriceSummaryByFilmTitleLike("EGG");
        System.out.println(filmSummary);
    }

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름과 영화 제목으로 조회")
    void test2() {
        List<FilmRentalSummary> test = filmRepository.findFilmRentalSummaryByFilmTitleLike("EGG");
        System.out.println(test);

    }

    @Test
    @DisplayName("")
    void test3() {
        List<Film> test = filmRepository.findRentedFilmByTitle("EGG");
        System.out.println(test);
    }
}
