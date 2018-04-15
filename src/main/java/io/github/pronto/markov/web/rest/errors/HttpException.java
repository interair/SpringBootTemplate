package io.github.pronto.markov.web.rest.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
class HttpException extends RuntimeException {

    private final String text;
    private final int status;
}
