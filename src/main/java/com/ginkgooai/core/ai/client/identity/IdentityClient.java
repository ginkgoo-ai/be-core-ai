package com.ginkgooai.core.ai.client.identity;

import com.ginkgooai.core.ai.client.identity.dto.response.UserResponse;
import com.ginkgooai.core.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "identity-service", url="${core-identity-uri}", configuration = FeignConfig.class)
public interface IdentityClient {
    /**
     * Searches for users based on email or name criteria
     * 
     * @param email Email address to search for (optional)
     * @param name User name to search for (optional)
     * @return Response containing matching user information
     */
    @GetMapping("/users")
    ResponseEntity<UserResponse> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name);

}

