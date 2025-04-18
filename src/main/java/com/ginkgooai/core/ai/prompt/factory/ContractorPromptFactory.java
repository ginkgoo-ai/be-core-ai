package com.ginkgooai.core.ai.prompt.factory;

public class ContractorPromptFactory extends PromptFactory {
    private static final String RESPONSE_FORMAT = """
        ## If contains contractors information, Response Format Requirements
        - Output First analyze the license classifications involved in the user's input statements, and output and display these license classifications.
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
        ## Contractor Matching Instructions:
        1. Please analyze strictly in accordance with the user's data and do not make any associations
        2. Analyze the project description to determine required license classifications
        3. If no distance is analyzed, set the default distance is 80467.2 meters
        4. Include detailed job description matching in response
        5. Provide 3-5 best matching contractors with complete details
        
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