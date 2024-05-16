package org.sight.jooqstart.config;

import org.jooq.JoinType;
import org.jooq.conf.ExecuteWithoutWhere;
import org.jooq.conf.RenderImplicitJoinType;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {
    @Bean
    public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
        return c -> c.settings()
                .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)
                .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)
                .withRenderSchema(false)
//                .withRenderImplicitJoinType(RenderImplicitJoinType.INNER_JOIN)
//                .withRenderImplicitJoinToManyType(RenderImplicitJoinType.LEFT_JOIN)
        ;
    }
}
