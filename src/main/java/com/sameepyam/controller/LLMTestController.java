package com.sameepyam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/health")
@RequiredArgsConstructor
public class LLMTestController {

    private final ChatClient chatClient;

    @GetMapping
    ResponseEntity<String> getHealth(){
        String response = chatClient.prompt("Respond OK if you see this message")
                .call()
                .content();
        return ResponseEntity.ok(response);
    }
}
