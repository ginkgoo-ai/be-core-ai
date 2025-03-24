package com.ginkgooai.core.ai.prompt.chat;

import com.ginkgooai.core.ai.prompt.PromptTemplate;

public interface ChatPrompt extends PromptTemplate {

    String SYSTEM_PROJECT = "# Project Create Role\n" +
            "# Create Project Roles\n" +
            "1. Check project name\n" +
            "2. Check project description\n" +
            "3. Check project port line\n" +
            "4. check project producer" +
            "# Before Create Project\n" +
            "1. Confirm user confirm information \n";
}
