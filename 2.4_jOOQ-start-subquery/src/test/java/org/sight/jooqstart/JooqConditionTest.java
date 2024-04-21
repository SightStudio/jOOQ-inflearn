package org.sight.jooqstart;

import org.assertj.core.api.Assertions;
import org.jooq.generated.tables.pojos.Actor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sight.jooqstart.actor.ActorFilmography;
import org.sight.jooqstart.actor.ActorFilmographySearchOption;
import org.sight.jooqstart.actor.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class JooqConditionTest {

    @Autowired
    private ActorRepository actorRepository;

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름으로 조회")
    void test() {
        // given
        var searchOption = ActorFilmographySearchOption.builder()
                .actorName("LOLLOBRIGIDA")
                .build();
        // when
        List<ActorFilmography> lollobrigida = actorRepository.findActorFilmographyByFullName(searchOption);

        // then
        Assertions.assertThat(lollobrigida).isNotEmpty();
    }

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름과 영화 제목으로 조회")
    void test2() {
        // given
        var searchOption = ActorFilmographySearchOption.builder()
                .actorName("LOLLOBRIGIDA")
                .filmTitle("COMMANDMENTS EXPRESS")
                .build();
        // when
        List<ActorFilmography> lollobrigida = actorRepository.findActorFilmographyByFullName(searchOption);

        // then
        Assertions.assertThat(lollobrigida).isNotEmpty();
    }

    @Test
    @DisplayName("")
    void test3() {
        List<Actor> lollobrigida = actorRepository.fetchActorByFirstNameOrLastName("LOLLOBRIGIDA", "LOLLOBRIGIDA");
    }
}
