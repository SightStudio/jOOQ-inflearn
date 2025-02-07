package org.sight.jooqcustompractice.config;

import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

	@Bean
	public DefaultConfigurationCustomizer jooqDefaultConfigurationCustomizer() {
		return c -> c.settings()
			.withRenderSchema(false);
	}
}
