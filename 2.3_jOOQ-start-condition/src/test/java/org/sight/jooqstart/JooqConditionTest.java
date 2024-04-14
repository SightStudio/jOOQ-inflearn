package org.sight.jooqstart;

import org.assertj.core.api.Assertions;
import org.jooq.generated.tables.pojos.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sight.jooqstart.category.CategoryRepository;
import org.sight.jooqstart.category.CategoryWithFilms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class JooqConditionTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("1) id 들로 카테고리 목록 조회")
    void test() {
        List<Category> categories = categoryRepository.findByIdIn(List.of(1L, 2L, 3L));

        Assertions.assertThat(categories)
                .asList()
                .hasSize(3);
    }

    @Test
    @DisplayName("2) 하위의 영화 정보 전체가 있는 카테고리를 이름으로 조회")
    void test2() {
        List<CategoryWithFilms> categoryList = categoryRepository.findCategoryWithFilmsByName("ACTION");
        Assertions.assertThat(categoryList).asList()
                .hasSize(3);
    }
}
