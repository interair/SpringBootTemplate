package io.github.pronto.markov.web.rest.errors;

/**
 * Simple exception with a message, that returns an Internal Server Error code.
 */
class InternalServerErrorException extends HttpException {

    public InternalServerErrorException(String message) {
        super(message, 500);
    }
}
