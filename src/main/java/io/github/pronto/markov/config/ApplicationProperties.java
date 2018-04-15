package io.github.pronto.markov.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "generate.application", ignoreUnknownFields = false)
@Data
public class ApplicationProperties {

    private AsyncProperties async = new AsyncProperties();
    private TokenProperties token = new TokenProperties();

    @Data
    public static class AsyncProperties {
        private int corePoolSize = 4;
        private int maxPoolSize = 8;
        private int queueCapacity = 100;
    }

    @Data
    public static class TokenProperties {
        private int tokenValidity = 600_000;
        private String secret = "secret";
    }
}
