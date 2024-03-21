package org.springframework.ai.aot.test.bedrock.anthropic;

import org.springframework.ai.bedrock.jurassic2.BedrockAi21Jurassic2ChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BedrockAi21Jurassic2AotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BedrockAi21Jurassic2AotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(BedrockAi21Jurassic2ChatClient chatClient) {
        return args -> {
            System.out.println("ChatClient: " + chatClient.getClass().getName());

            // Synchronous Chat Client
            String response = chatClient.call("Tell me a joke.");
            System.out.println("SYNC RESPONSE: " + response);
        };
    }
}
