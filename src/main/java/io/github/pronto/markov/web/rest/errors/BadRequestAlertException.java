package io.github.pronto.markov.web.rest.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestAlertException extends HttpException {

    private final String entityName;
    private final String errorKey;

    public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage, 400);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }
}
