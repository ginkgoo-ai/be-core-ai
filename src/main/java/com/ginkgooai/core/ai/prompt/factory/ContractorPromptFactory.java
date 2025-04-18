package com.ginkgooai.core.ai.prompt.factory;

public class ContractorPromptFactory extends PromptFactory {
    private static final String RESPONSE_FORMAT = """
        ## If contains contractors information, Response Format Requirements
        - Output must be in JSON format enclosed in ```card``` markers, please check it again
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
        1. Please analyze strictly in accordance with the user's data and do not make any associations, when user mentions any of these, automatically search for subcontractors:
           - Address/location details
           - License classification requirements
           - Distance/proximity considerations
        2. Analyze the project description to determine required license classifications
        3. If no distance is analyzed, set the default distance is 80467.2 meters
        4. Prioritize contractors by:
           - License match (primary)
           - Distance from project location (secondary)
           - Availability date (tertiary)
           - Customer rating (quaternary)
        5. Include detailed job description matching in response
        6. Always verify contractor license status with CSLB database
        7. Provide 3-5 best matching contractors with complete details
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