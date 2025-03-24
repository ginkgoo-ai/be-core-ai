package com.ginkgooai.core.ai.client.identity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@Schema(description = "User response data transfer object")
public class UserResponse {
    @Schema(description = "User's unique identifier")
    private String id;

    @Schema(description = "User's subject identifier from social provider")
    private String sub;

    @Schema(description = "User's email address")
    private String email;

    @Schema(description = "User's first name")
    private String firstName;

    @Schema(description = "User's last name")
    private String lastName;
    
    @Schema(description = "Unique user name")
    private String name;

    @Schema(description = "Indicates whether user has completed their profile setup",
            example = "true")
    private boolean enabled;

    @Schema(description = "Logo file")
    private String picture;

    @Schema(description = "Set of user's role names",
            example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;

}