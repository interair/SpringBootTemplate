package io.github.pronto.markov.web.rest.errors;


class EmailAlreadyUsedException extends HttpException {

    public EmailAlreadyUsedException() {
        super("Email address already in use", 400);
    }
}
