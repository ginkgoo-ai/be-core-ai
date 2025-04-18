package com.ginkgooai.core.ai.prompt.factory;

public class CommonPromptFactory extends PromptFactory{

    private static final String RESPONSE_FORMAT = """
        ## If contains contractors information response Format Requirements
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

    @Override
    public String getBusinessFormat() {
        return "";
    }

    @Override
    public String getResponseFormat() {
        return RESPONSE_FORMAT;
    }
}
