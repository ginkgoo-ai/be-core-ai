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
}
