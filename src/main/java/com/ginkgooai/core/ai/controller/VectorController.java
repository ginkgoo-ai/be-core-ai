package com.ginkgooai.core.ai.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/vector")
public class VectorController {

    final VectorStore vectorStore;

    @PostMapping
    public String write(@RequestParam String text) {
        vectorStore.write(List.of(Document.builder().text(text).build()));
        return "ok";
    }

    @GetMapping
    public String writeWorkFlow(){
        // 预置任务流程文档（网页4分块策略）
        List<Document> flows = List.of(
                new Document("""
                【标准项目创建流程】
                步骤：
                1. 询问是否确认创建项目
                2. 调用mcp 接口项目创建
                """, Map.of("category", "project_creation"))
        );

        vectorStore.add(flows);
        return "ok";
    }
}
