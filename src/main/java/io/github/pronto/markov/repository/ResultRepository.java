package io.github.pronto.markov.repository;

import io.github.pronto.markov.domain.Result;
import io.github.pronto.markov.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    Page<Result> findAllByUser(Pageable pageable, User user);

}
