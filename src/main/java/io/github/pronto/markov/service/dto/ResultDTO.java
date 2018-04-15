package io.github.pronto.markov.service.dto;

import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.pronto.markov.domain.Result;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true, builderClassName = "ResBuilder")
@JsonDeserialize(builder = ResultDTO.ResBuilder.class)
public class ResultDTO {

    @Builder.Default private final Optional<Long> id = Optional.empty();
    @Size(min = 5, max = 10_000)
    @NotBlank
    private final String data;
    @NotNull
    @Positive
    private final Integer suffixSize;
    @NotNull
    @Positive
    private final Integer resultSize;

    private final Optional<String> result;

    public static ResultDTO toDto(Result res) {
        return ResultDTO.builder()
            .id(Optional.of(res.getId()))
            .data(res.getData())
            .result(Optional.of(res.getResult()))
            .suffixSize(res.getSuffixSize())
            .resultSize(res.getResultSize())
            .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class ResBuilder {
    }

}
