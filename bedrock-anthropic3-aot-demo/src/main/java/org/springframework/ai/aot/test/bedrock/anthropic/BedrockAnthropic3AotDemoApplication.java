package org.springframework.ai.aot.test.bedrock.anthropic;

import java.util.stream.Collectors;

import reactor.core.publisher.Flux;

import org.springframework.ai.bedrock.anthropic3.BedrockAnthropic3ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BedrockAnthropic3AotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BedrockAnthropic3AotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(BedrockAnthropic3ChatClient chatClient) {
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

        };
    }
}
