package com.ginkgooai.core.ai.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.utils.JsonUtils;
import com.ginkgooai.core.common.utils.ContextUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PromptBuilder {
    
    private String userInputText;
    
    private List<AssistantParams> assistantParams;
    
    @Builder
    @Data
    @AllArgsConstructor
    static class AssistantParams {
        String key;
        String value;
    }
    
    /**
     * Creates a JSON representation of the prompt with user input and assistant parameters
     * 
     * @param userInputText The text input from the user
     * @return JSON string containing the prompt structure
     * @throws JsonProcessingException if an error occurs during JSON processing
     */
    public static String create(String userInputText) throws JsonProcessingException {
        return JsonUtils.toJson(PromptBuilder.builder()
                .userInputText(userInputText)
                .assistantParams(initAssistantParams(null)).build());
    }


    public static String create(String userInputText, String workspaceId) throws JsonProcessingException {
        return JsonUtils.toJson(PromptBuilder.builder()
                .userInputText(userInputText)
                .assistantParams(initAssistantParams(workspaceId)).build()).replace("{", "\\{").replace("}", "\\}");
    }

    /**
     * Initializes the assistant parameters with authentication token and workspace ID
     * 
     * @return List of assistant parameters
     */
    private static List<AssistantParams> initAssistantParams(String workspaceId) throws JsonProcessingException {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        log.info("attributes:{}, workspace_id: {}", attributes.getRequest()
                .getHeader(HttpHeaders.AUTHORIZATION), ContextUtils.getWorkspaceId());
        return Arrays.asList(
               AssistantParams.builder()
                       .key(PromptTemplate.TOKEN)
                       .value(attributes.getRequest()
                       .getHeader(HttpHeaders.AUTHORIZATION)).build(),
               AssistantParams.builder()
                       .key(PromptTemplate.WORKSPACE_ID)
                       .value(workspaceId == null ? ContextUtils.getWorkspaceId() : workspaceId).build()
               );
    }
}
