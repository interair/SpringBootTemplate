package io.github.pronto.markov.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import io.github.pronto.markov.repository.UserRepository;
import io.github.pronto.markov.security.annotation.AdminRole;
import io.github.pronto.markov.service.UserService;
import io.github.pronto.markov.service.dto.UserDTO;
import io.github.pronto.markov.web.rest.errors.BadRequestAlertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
class UserResource {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/users")
    @AdminRole
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDTO userDTO,
        @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String auth /** for swagger**/) throws URISyntaxException
    {
        log.debug("REST request to save User : {}", userDTO);
        userDTO.getId().ifPresent(i -> {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
        });
        if (userRepository.existsByEmailIgnoreCase(userDTO.getEmail())) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
        }
        return ResponseEntity.created(new URI("/api/users/" + userService.createUser(userDTO).getEmail())).build();
    }

    @GetMapping("/users")
    public List<UserDTO> listUsers(Pageable pageable,
        @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String auth /** for swagger**/)
    {
        return userService.getAllManagedUsers(pageable);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserDTO> getUsers(@PathVariable String email,
        @RequestHeader(value = AUTHORIZATION_HEADER, required = false) String auth /** for swagger**/)
    {
        return userService.getUser(email)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/authorities")
    @AdminRole
    public List<String> getAuthorities(@RequestHeader(value = AUTHORIZATION_HEADER, required = false) String auth /** for swagger**/) {
        return userService.getAuthorities();
    }

    @DeleteMapping("/users/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AdminRole
    public void deleteUser(@PathVariable String email) {
        log.debug("REST request to delete User: {}", email);
        userService.deleteUser(email);
    }
}
