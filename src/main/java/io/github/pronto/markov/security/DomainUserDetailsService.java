package io.github.pronto.markov.security;

import com.codahale.metrics.annotation.Timed;
import io.github.pronto.markov.domain.Authority;
import io.github.pronto.markov.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@AllArgsConstructor
@Slf4j
class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    @Timed
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase();
        return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(lowercaseLogin)
            .map(u -> new User(u.getEmail(), u.getPassword(), convert(u.getAuthorities())))
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }

    private Collection<? extends GrantedAuthority> convert(Set<Authority> authorities) {
        return authorities.stream().map(Authority::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

}
