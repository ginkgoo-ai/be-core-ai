package com.ginkgooai.core.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record EvaluatorRecord(
        @Schema(description = "Evaluator result of the record")
        @JsonProperty(required = true, value = "score")
        ScoreType score) {

    public enum ScoreType
    {
        @JsonProperty("LOW")
        LOW,
        @JsonProperty("MIDDLE")
        MIDDLE,
        @JsonProperty("HIGH")
        HIGH
    }
}
