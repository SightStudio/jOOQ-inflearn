package org.sight.gradletestcontainerflyway;

import org.jooq.DSLContext;
import org.jooq.generated.tables.Film;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GradleTestcontainerFlywayApplicationTests {

	DSLContext dslContext;

	@Test
	void contextLoads() {
		var film = Film.FILM;

		List<Film> films = dslContext.selectFrom(film)
				.fetchInto(Film.class);

	}

}
