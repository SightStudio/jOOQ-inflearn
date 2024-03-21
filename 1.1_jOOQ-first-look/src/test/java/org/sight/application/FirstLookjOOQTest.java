package org.sight.application;

import org.jooq.DSLContext;
import org.jooq.Routine;
import org.jooq.generated.Routines;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FirstLookjOOQTest {

    @Autowired
    private DSLContext dslContext;

    @Test
    void test() {

    }
}
