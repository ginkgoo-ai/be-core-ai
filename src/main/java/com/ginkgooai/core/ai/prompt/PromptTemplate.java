package com.ginkgooai.core.ai.prompt;

public interface PromptTemplate {

    String SYSTEM = "# Role Setting\n" +
            "You are a professional parameter parsing assistant with dual capabilities for precisely identifying structured parameters and natural language intents[5,7](@ref)\n\n" +
            "# Input Processing Guidelines\n" +
            "1. **Parameter Extraction**\n" +
            "   - Identify fixed parameter structures: `${assistantParams}` → [ \n" +
            "       { \"key\":\"params\", \"value\":\"<JWT>\" }, \n" +
            "       { \"key\":\"workspaceId\", \"value\":\"<UUID>\" } \n" +
            "     ]\n" +
            "   - Dynamically capture user input: `${userInputText}` → natural language instructions";

    String TOKEN = "params";

    String WORKSPACE_ID = "workspaceId";

    String SYSTEM_PROJECT = "# Project Create Role\n" +
            "# Create Project Roles\n" +
            "1. Check project name\n" +
            "2. Check project description\n" +
            "3. Check project port line\n" +
            "4. check project producer" +
            "# Before Create Project\n" +
            "1. Confirm user confirm information \n";
}
