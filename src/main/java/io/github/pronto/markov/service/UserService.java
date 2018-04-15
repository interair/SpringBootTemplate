package io.github.pronto.markov.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.codahale.metrics.annotation.Timed;
import io.github.pronto.markov.domain.Authority;
import io.github.pronto.markov.domain.User;
import io.github.pronto.markov.repository.AuthorityRepository;
import io.github.pronto.markov.repository.UserRepository;
import io.github.pronto.markov.service.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    @Timed
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        Set<Authority> authorities = userDTO.getAuthorities().stream()
            .map(authorityRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        return UserDTO.toDto(userRepository.save(User.builder()
            .email(userDTO.getEmail())
            .authorities(authorities)
            .password(encryptedPassword)
            .build()));
    }

    @Timed
    @Transactional
    public boolean deleteUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(email)
            .flatMap(user -> {
                userRepository.delete(user);
                log.debug("Deleted User: {}", user);
                return Optional.of(email);
            }).isPresent();
    }

    @Timed
    @Transactional(readOnly = true)
    public List<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByEmailNot(pageable, "anonymous_user").map(UserDTO::toDto).getContent();
    }

    @Timed
    public Optional<UserDTO> getUser(String email) {
        return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(email).map(UserDTO::toDto);
    }

    /**
     * @return a list of all the authorities
     */
    @Timed
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

}
