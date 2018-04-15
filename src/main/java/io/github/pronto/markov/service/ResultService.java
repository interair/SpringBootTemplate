package io.github.pronto.markov.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;
import io.github.pronto.markov.domain.Result;
import io.github.pronto.markov.repository.ResultRepository;
import io.github.pronto.markov.repository.UserRepository;
import io.github.pronto.markov.service.dto.ResultDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    @Timed
    public ResultDTO calculate(@Valid ResultDTO res, String email) {
        return ResultDTO.toDto(resultRepository.save(Result.builder()
            .user(userRepository.findOneWithAuthoritiesByEmailIgnoreCase(email).get())
            .data(res.getData())
            .result(MarkovGenerator.generate(res.getData(), res.getResultSize(), res.getSuffixSize()))
            .suffixSize(res.getSuffixSize())
            .resultSize(res.getResultSize())
            .build()));
    }

    @Timed
    @Transactional
    public boolean deleteResult(Long id) {
        return resultRepository.findById(id)
            .flatMap(res -> {
                resultRepository.delete(res);
                log.debug("Deleted Res: {}", res);
                return Optional.of(res);
            }).isPresent();
    }

    @Timed
    public List<ResultDTO> getAllUserResults(Pageable pageable, String name) {
        return resultRepository.findAllByUser(pageable, userRepository.findOneWithAuthoritiesByEmailIgnoreCase(name).get())
            .stream().map(ResultDTO::toDto).collect(Collectors.toList());
    }

}
