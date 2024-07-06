package com.sight.kotlin.jooqwithkotlin.config

import org.jooq.conf.ExecuteWithoutWhere
import org.jooq.conf.RenderImplicitJoinType
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JooqConfig {
    @Bean
    fun jooqDefaultConfigurationCustomizer(): DefaultConfigurationCustomizer {
        return DefaultConfigurationCustomizer { c ->
            c.settings()
                // where 절 없이 delete, update 실행 금지
                .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)
                .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)

                // implicit join 사용 금지
                .withRenderImplicitJoinType(RenderImplicitJoinType.THROW)
                .withRenderImplicitJoinToManyType(RenderImplicitJoinType.THROW)

                // schema 미포함
                .withRenderSchema(false)
        }
    }
}
