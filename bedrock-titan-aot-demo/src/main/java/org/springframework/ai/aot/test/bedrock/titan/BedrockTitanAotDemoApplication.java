package org.springframework.ai.aot.test.bedrock.titan;

import java.util.List;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;

import org.springframework.ai.bedrock.titan.BedrockTitanChatClient;
import org.springframework.ai.bedrock.titan.BedrockTitanEmbeddingClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BedrockTitanAotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BedrockTitanAotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(BedrockTitanChatClient chatClient,
            BedrockTitanEmbeddingClient embeddingClient) {
        return args -> {
            System.out.println("ChatClient: " + chatClient.getClass().getName());

            // Synchronous Chat Client
            String response = chatClient.call("Tell me a joke.");
            System.out.println("SYNC RESPONSE: " + response);

            // Streaming Chat Client
            Flux<ChatResponse> flux = chatClient.stream(new Prompt("Tell me a joke."));
            String fluxResponse = flux.collectList().block().stream().map(r -> r.getResult().getOutput().getContent())
                    .collect(Collectors.joining());
            System.out.println("ASYNC RESPONSE: " + fluxResponse);

            // Embedding Client
            List<List<Double>> embeddings = embeddingClient.embed(List.of("Hello", "World"));
            System.out.println("EMBEDDINGS SIZE: " + embeddings.size());

        };
    }
}
