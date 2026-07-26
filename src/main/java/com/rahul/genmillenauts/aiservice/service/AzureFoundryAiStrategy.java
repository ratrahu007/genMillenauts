package com.rahul.genmillenauts.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openai.azure.credential.AzureApiKeyCredential;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("azure")
public class AzureFoundryAiStrategy implements AiStrategy {

    private final OpenAIClient client;
    private final String deploymentName;

    public AzureFoundryAiStrategy(
            @Value("${ai.azure.endpoint}") String endpoint,
            @Value("${ai.azure.api.key}") String apiKey,
            @Value("${ai.azure.deployment}") String deploymentName) {

        this.deploymentName = deploymentName;

        this.client = OpenAIOkHttpClient.builder()
                .baseUrl(endpoint)
                .credential(AzureApiKeyCredential.create(apiKey))
                .build();
    }

    @Override
    public String generateContent(String prompt) {

        try {

            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(deploymentName)
                    .instructions("""
You are GenMillenauts AI, a compassionate mental wellness assistant.

Rules:
- Never introduce yourself as ChatGPT or OpenAI.
- Always identify yourself as GenMillenauts AI.
- Be empathetic and supportive.
- Keep responses concise and easy to understand.
- If the user expresses thoughts of self-harm or suicide, encourage them to contact a trusted person or mental health professional immediately.
- Do not diagnose medical conditions.
""")
                    .input(prompt)
                    .build();

            Response response = client.responses().create(params);

            StringBuilder answer = new StringBuilder();

            response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .forEach(text -> answer.append(text.text()));

            log.info("Azure AI Foundry response generated successfully.");

            return answer.toString();

        } catch (Exception e) {

            log.error("Azure AI Foundry Error", e);
            throw new RuntimeException("Unable to generate AI response.", e);

        }

    }
}
