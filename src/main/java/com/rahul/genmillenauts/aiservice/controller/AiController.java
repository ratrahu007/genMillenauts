package com.rahul.genmillenauts.aiservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rahul.genmillenauts.aiservice.dto.*;
import com.rahul.genmillenauts.aiservice.service.GenerativeAiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GenerativeAiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request,
                                             @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(aiService.getBotReply(request, userId));
    }
}
