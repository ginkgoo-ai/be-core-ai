package com.ginkgooai.core.ai.prompt.factory;

import com.ginkgooai.core.ai.dto.QuickCommand;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class PromptFactoryManager {
    
    public static PromptFactory getFactory(QuickCommand command) {
        if (command == QuickCommand.CONTRACTORS_INFO) {
            return new ContractorPromptFactory();
        }
        return new ContractorPromptFactory();
    }


    public static PromptFactory getFactory(List<QuickCommand> commands) {

        if (CollectionUtils.isEmpty(commands)) {
            return new ContractorPromptFactory();
        }

        if (commands.contains(QuickCommand.CONTRACTORS_INFO)) {
            return new ContractorPromptFactory();
        }
        return new ContractorPromptFactory();
    }
}