package com.sameepyam.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the single application-wide {@link ChatClient} from Spring AI's auto-configured
 * {@code ChatClient.Builder}. The default system prompt sets the warm, plain-language persona that
 * every feature shares; individual features (Scam Detector, Document Explainer, ...) add their own
 * task-specific system text on top of this at the call site.
 *
 * <p>Forward-compatibility: keep feature-specific prompt assembly in the services, not here. Phase 1
 * (RAG) and Phase 1.5 (tool use) extend the per-call prompt/tools, not this baseline persona.
 */
@Configuration
public class ChatClientConfig {

    private static final String DEFAULT_SYSTEM_PROMPT = """
            You are a calm, trusted helper for elderly people who are not confident in English.
            Speak simply and warmly, like a patient family member. Use short sentences and everyday
            words — never jargon. Never be alarming. Always end with one clear, reassuring next step.
            """;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(DEFAULT_SYSTEM_PROMPT)
                .build();
    }
}