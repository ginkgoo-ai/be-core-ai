package com.ginkgooai.core.ai.prompt.workflow;

public interface WorkflowPrompt {

    String DEFAULT= """
            ###Role: You are a business workflow helper, According to the user's workflow description.
            
            ###Tools:
            ##1. RequirementAnalyzer:
            ##    - Input: userInput
            ##    - Output: steps
            ##    - Description: Analyze the user's requirements and return the corresponding steps.
            
            ###Question: {userInput}
            
            ##Thought1: According to the user input information, disassemble into the corresponding steps
            
            ##Action 1: RequirementAnalyzer
            ##Input: {userInput}
            ##Output: {steps}
            
            ###Final Answer:
            ## steps
            
            """;
}
