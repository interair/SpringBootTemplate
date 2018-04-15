package io.github.pronto.markov.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import io.github.pronto.markov.security.annotation.AdminRole;
import io.github.pronto.markov.service.ResultService;
import io.github.pronto.markov.service.dto.ResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static io.github.pronto.markov.security.jwt.JWTConfigurer.AUTHORIZATION_HEADER;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
class ResultResource {

    private final ResultService resultService;

    @PostMapping("/results")
    @AdminRole
    public ResponseEntity<Void> createUser(@Valid @RequestBody ResultDTO resultDTO, Authentication authentication,
        @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String auth /** for swagger**/) throws URISyntaxException
    {
        ResultDTO calculate = resultService.calculate(resultDTO, authentication.getName());
        return ResponseEntity.created(new URI("/api/result/" + calculate.getId().get())).build();
    }

    @GetMapping("/results")
    public List<ResultDTO> listResults(Pageable pageable, Authentication authentication,
        @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String auth /** for swagger**/)
    {
        return resultService.getAllUserResults(pageable, authentication.getName());
    }

    @DeleteMapping("/results/{id}")
    @AdminRole
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResult(@PathVariable Long id) {
        log.debug("REST request to delete User: {}", id);
        resultService.deleteResult(id);
    }
}
