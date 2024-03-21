package org.springframework.ai.aot.test.mistralai;

import java.util.stream.Collectors;

import reactor.core.publisher.Flux;

import org.springframework.ai.anthropic.AnthropicChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AnthropicAotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AnthropicAotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(AnthropicChatClient chatClient) {
        return args -> {

            // Synchronous Chat Client
            String response = chatClient.call("Tell me a joke.");
            System.out.println("SYNC RESPONSE: " + response);

            // Streaming Chat Client
            Flux<ChatResponse> flux = chatClient.stream(new Prompt("Tell me a joke."));
            String fluxResponse = flux.collectList().block().stream()
                    .filter(r -> r.getResult() != null)
                    .map(r -> r.getResult().getOutput().getContent())
                    .collect(Collectors.joining());
            System.out.println("ASYNC RESPONSE: " + fluxResponse);

        };
    }
}
