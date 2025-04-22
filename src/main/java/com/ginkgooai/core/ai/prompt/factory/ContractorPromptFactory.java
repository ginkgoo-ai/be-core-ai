package com.ginkgooai.core.ai.prompt.factory;

public class ContractorPromptFactory extends PromptFactory {
    private static final String RESPONSE_FORMAT = """
        ## If contains contractors information, Response Format Requirements
        - Output First analyze the license classifications involved in the user's input statements.
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
        ## ReAct Framework:
        ### Thought Process:
        1. Analyze user's project description strictly without making assumptions
        2. Determine required CSLB license classifications
        3. Evaluate geographic proximity and availability
        4. Prepare appropriate response
        
        ### Action Steps:
        1. Always analyze project requirements objectively
        2. If and only if both of the following conditions are met:
               2.1. The "address" field exists and contains valid information (not empty or placeholder),
               2.2. No valid calculation or analysis has been performed for "radius" (field is empty/undefined/missing),THEN set the default radius value to 80467.2 meters.
        3. Include detailed job description matching in response
        4. Provide 3-5 best matching contractors with complete details
        
        ## Output Requirements:
        - Use JSON format for contractor information
        - Include all mandatory fields (businessName, licenseNumber, etc.)
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