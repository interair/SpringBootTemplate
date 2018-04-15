package io.github.pronto.markov.service.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.pronto.markov.domain.Authority;
import io.github.pronto.markov.domain.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true, builderClassName = "UserBuilder")
@JsonDeserialize(builder = UserDTO.UserBuilder.class)
public class UserDTO {

    @Builder.Default private final Optional<Long> id = Optional.empty();
    @Email
    @Size(min = 5, max = 100)
    @NotBlank
    private final String email;
    private final String createdBy;
    private final Instant createdDate;
    private final String lastModifiedBy;
    @Size(min = 5, max = 100)
    private final String password;
    private final Instant lastModifiedDate;
    @Builder.Default private Set<String> authorities = Collections.emptySet();

    public static UserDTO toDto(User user) {
        return UserDTO.builder()
            .id(Optional.ofNullable(user.getId()))
            .email(user.getEmail())
            .createdBy(user.getCreatedBy())
            .createdDate(user.getCreatedDate())
            .lastModifiedBy(user.getLastModifiedBy())
            .lastModifiedDate(user.getLastModifiedDate())
            .authorities(user.getAuthorities().stream()
                .map(Authority::getName)
                .collect(Collectors.toSet()))
            .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class UserBuilder {
    }

}
