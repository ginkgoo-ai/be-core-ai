package com.ginkgooai.core.ai.tools;

import com.ginkgooai.core.ai.utils.SpringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

public class VectorTools {

    @Tool(description = "add input to vector store")
    String addVector(String input){
        try{
            SpringUtils.getBean(VectorStore.class).write(List.of(Document.builder().text(input).build()));
        }catch (Exception e){
            return "add vector failed.";
        }
        return "ok";
    }
}
