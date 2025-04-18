package com.ginkgooai.core.ai.prompt.factory;

import com.ginkgooai.core.ai.dto.QuickCommand;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class PromptFactoryManager {
    
    public static PromptFactory getFactory(QuickCommand command) {
        if (command == QuickCommand.CONTRACTORS_INFO) {
            return new ContractorPromptFactory();
        }
        return new CommonPromptFactory();
    }


    public static PromptFactory getFactory(List<QuickCommand> commands) {

        if (CollectionUtils.isEmpty(commands)) {
            return new CommonPromptFactory();
        }

        if (commands.contains(QuickCommand.CONTRACTORS_INFO)) {
            return new ContractorPromptFactory();
        }
        // 默认返回ContractorPromptFactory，后续可以扩展其他工厂
        return new CommonPromptFactory();
    }
}