package io.github.pronto.markov.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JacksonConfiguration {

    @Bean
    Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

    @Bean
    AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

}
