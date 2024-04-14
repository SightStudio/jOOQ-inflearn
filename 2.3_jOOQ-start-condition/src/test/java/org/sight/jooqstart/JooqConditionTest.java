package org.sight.jooqstart;

import org.junit.jupiter.api.Test;
import org.sight.jooqstart.actor.ActorFilmography;
import org.sight.jooqstart.actor.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class JooqConditionTest {

    @Autowired
    private ActorRepository actorRepository;

    @Test
    void test() {
        List<ActorFilmography> lollobrigida = actorRepository.findActorFilmographyByFullName("LOLLOBRIGIDA");
        System.out.println(lollobrigida);
    }
}
