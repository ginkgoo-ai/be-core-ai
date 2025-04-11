package com.ginkgooai.core.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record ClassifyRecord(
        @Schema(description = "The classification result of the record, ")
        @JsonProperty(required = true, value = "steps")
        StepsType steps) {

    public enum StepsType
    {
        @JsonProperty("PROJECT")
        PROJECT,
        @JsonProperty("EXECUTION")
        EXECUTION,
        @JsonProperty("WORKFLOW")
        WORKFLOW,
        @JsonProperty("COMMON")
        COMMON
    }
}
