package com.ginkgooai.core.ai.prompt.factory;

public class ContractorPromptFactory extends PromptFactory {
    private static final String RESPONSE_FORMAT = """
        ## Response Format Requirements
        - Output must be in JSON format enclosed in ```card``` markers
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
        """;

    private static final String CONTRACTOR_PROMPT = """
        ## Contractor Matching Instructions:
        1. Analyze the project description to determine required license classifications
        2. Prioritize contractors by:
           - License match (primary)
           - Distance from project location (secondary)
           - Availability date (tertiary)
           - Customer rating (quaternary)
        3. Include detailed job description matching in response
        4. Always verify contractor license status with CSLB database
        5. Provide 3-5 best matching contractors
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