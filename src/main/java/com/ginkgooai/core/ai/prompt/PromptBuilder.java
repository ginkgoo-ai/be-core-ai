package com.ginkgooai.core.ai.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ginkgooai.core.ai.utils.JsonUtils;
import com.ginkgooai.core.common.utils.ContextUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    
    public static String create(String userInputText) throws JsonProcessingException {

        return JsonUtils.toJson(PromptBuilder.builder()
                .userInputText(userInputText)
                .assistantParams(initAssistantParams()).build());
    }

    private static List<AssistantParams> initAssistantParams() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

       return Arrays.asList(
               AssistantParams.builder()
                       .key(PromptTemplate.TOKEN)
                       .value(attributes.getRequest()
                       .getHeader(HttpHeaders.AUTHORIZATION)).build(),
               AssistantParams.builder()
                       .key(PromptTemplate.WORKSPACE_ID)
                       .value(ContextUtils.get().getWorkspaceId()).build()
               );
    }
}
