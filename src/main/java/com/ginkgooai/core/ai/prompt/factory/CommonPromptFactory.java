package com.ginkgooai.core.ai.prompt.factory;

public class CommonPromptFactory extends PromptFactory{
    @Override
    public String getBusinessFormat() {
        return "";
    }

    @Override
    public String getResponseFormat() {
        return "If the output contains list items, please use markdown format to output.";
    }
}
