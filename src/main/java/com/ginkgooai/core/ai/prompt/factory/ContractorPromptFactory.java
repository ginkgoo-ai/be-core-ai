package com.ginkgooai.core.ai.prompt.factory;

public class ContractorPromptFactory extends PromptFactory {
    private static final String RESPONSE_FORMAT = """
        ## Output Format Requirements
          If contains contractors list information, response Format Requirements
            - Output First analyze the license classifications involved in the user's input statements.
            - Output the license classifications found in the user's input statements and the reason.
            - Output must be in JSON format enclosed in ```card``` markers, please check beginning with ```card again
            - Output must contain:
              * type: "card" (fixed value)
              * content: detailed contractors information
            - Content must include these mandatory fields:
              * businessName: Legal business name
              * licenseNumber: CSLB license number (format: 8 digits)
              * address: Full business address
              * city: city
              * state: state
              * zip: zip code
              * phoneNumber: Contact number (format: (XXX) XXX-XXXX)
              * classification: License classification (e.g. B, C-10)
            - please check it again
       
            Example:
        
                ```card
                        {
                          "type": "card",
                          "content": [{
                            "businessName": "Legal business name",
                            "licenseNumber": "CSLB license number (format: 8 digits)",
                            "address": "Full business address",
                            "city": "city",
                            "state": "state",
                            "zip": "zip code",
                            "phoneNumber": "Contact number (format: (XXX) XXX-XXXX)",
                            "classification": "License classification (e.g. B, C-10)"
                          }]
                        }
                ```
        """;

    private static final String CONTRACTOR_PROMPT = """
        ## Thought Process:
             1. Analyze the user's input to understand their needs
             2. Identify required CSLB license classifications from the user's input
             3. Repeat the analysis of the classifications involved in the description

        ## Action Steps:
             ### Step1: Analyze the licenses in the user input
                1: Check whether the semantics are included C-Class codes (e.g., C-10, C-35), if found C-Class license classifications immediately invoke step2
                2: **Only** no C-Class matches, Check whether the semantics are included B-General License.

             ### Step2: Check whether it is necessary set radius default value
                If and only if both of the following conditions are met:
                   1: The "address" field exists and contains valid information (not empty or placeholder),
                   2.2. No valid calculation or analysis has been performed for "radius" (field is empty/undefined/missing),THEN set the default radius value to 80467.2 meters.
        
             ### Step3: Check parameters
                1.Check the classification analyzed weather includes C-Class codes (e.g., C-10, C-35),
                  if found C-Class license classifications only use C-Class codes (e.g., C-10, C-35) to search contractors.
 
             ### Step4:
                1.If have C-Class matches, only use C-Class License to search contractors
                2.provide 3-5 best matching contractors with complete details
        
        ## Observation
            1. If contains contractors list information, response Format Requirements
                - Output must be in JSON format enclosed in ```card``` markers, please check beginning with ```card again
        
        """;

    @Override
    public String getBusinessFormat() {
        return CONTRACTOR_PROMPT;
    }

    @Override
    public String getResponseFormat() {
        return RESPONSE_FORMAT;
    }
}