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

import java.util.Collections;
import java.util.List;

@SpringBootTest
public class JooqConditionTest {

    @Autowired
    private ActorRepository actorRepository;

    @Test
    @DisplayName("and 조건 검색 - fistName와 LastName이 일치하는 배우 조회")
    void AND조건_1() {
        // given
        String firstName = "ED";
        String lastName = "CHASE";

        // when
        List<Actor> actorList = actorRepository.findByFirstnameAndLastName(firstName, lastName);

        // then
        Assertions.assertThat(actorList).hasSize(1);
    }

    @Test
    @DisplayName("or 조건 검색 - fistName 또는 LastName이 일치하는 배우 조회")
    void or조건_1() {
        // given
        String firstName = "ED";
        String lastName = "CHASE";

        // when
        List<Actor> actorList = actorRepository.findByFirstnameOrLastName(firstName, lastName);

        // then
        Assertions.assertThat(actorList).hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("in절 - 동적 조건 검색")
    void in절_동적_조건검색_1() {
        // when
        List<Actor> actorList = actorRepository.findByActorIdIn(List.of(1L));

        // then
        Assertions.assertThat(actorList).hasSize(1);
    }

    @Test
    @DisplayName("in절 - 동적 조건 검색 - empty list시 제외")
    void in절_동적_조건검색_2() {
        // when
        List<Actor> actorList = actorRepository.findByActorIdIn(Collections.emptyList());

        // then
        Assertions.assertThat(actorList).hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름으로 조회")
    void 다중_조건_검색_1() {
        // given
        var searchOption = ActorFilmographySearchOption.builder()
                .actorName("LOLLOBRIGIDA")
                .build();
        // when
        List<ActorFilmography> actorFilmographies = actorRepository.findActorFilmography(searchOption);

        // then
        Assertions.assertThat(actorFilmographies).hasSize(1);
    }

    @Test
    @DisplayName("다중 조건 검색 - 배우 이름과 영화 제목으로 조회")
    void 다중_조건_검색_2() {
        // given
        var searchOption = ActorFilmographySearchOption.builder()
                .actorName("LOLLOBRIGIDA")
                .filmTitle("COMMANDMENTS EXPRESS")
                .build();

        // when
        List<ActorFilmography> actorFilmographies = actorRepository.findActorFilmography(searchOption);

        // then
        Assertions.assertThat(actorFilmographies).hasSize(1);
        Assertions.assertThat(actorFilmographies.get(0).getFilmList()).hasSize(1);
    }
}
