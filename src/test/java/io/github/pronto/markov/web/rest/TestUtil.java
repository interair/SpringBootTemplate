package io.github.pronto.markov.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * Utility class for testing REST controllers.
 */
class TestUtil {

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
    }

    public static String convertToJson(Object object)
            throws IOException {
        return mapper.writeValueAsString(object);
    }

}
