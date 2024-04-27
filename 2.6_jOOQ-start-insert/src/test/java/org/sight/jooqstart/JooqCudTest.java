package org.sight.jooqstart;

import org.jooq.generated.tables.pojos.Actor;
import org.junit.jupiter.api.Test;
import org.sight.jooqstart.actor.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
public class JooqCudTest {

    @Autowired
    ActorRepository actorRepository;

    @Test
    @Transactional
    void test() {
        var actor = new Actor();
        actor.setFirstName("John");
        actor.setLastName("Doe");
        actor.setLastUpdate(LocalDateTime.now());
//        actorRepository.saveByDao(actor);

        Actor save = actorRepository.saveWithReturningByActiveRecord(actor);
        Long saveWithReturningPkOnly = actorRepository.saveWithReturningPkOnly(actor);

        System.out.println(actor);
    }
}
