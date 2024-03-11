package org.springframework.ai.aot.test.vertexai.palm2;

import java.util.List;

import org.springframework.ai.vertexai.palm2.VertexAiPaLm2ChatClient;
import org.springframework.ai.vertexai.palm2.VertexAiPaLm2EmbeddingClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VertexAiPaLM2AotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(VertexAiPaLM2AotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(VertexAiPaLm2ChatClient chatClient, VertexAiPaLm2EmbeddingClient embeddingClient) {
        return args -> {
            System.out.println("ChatClient: " + chatClient.getClass().getName());
            System.out.println("EmbeddingClient: " + embeddingClient.getClass().getName());

            // Synchronous Chat Client
            String response = chatClient.call("Tell me a joke.");
            System.out.println("SYNC RESPONSE: " + response);


            // Embedding Client
            System.out.println(embeddingClient.embed("Hello, World!"));
            List<List<Double>> embeddings = embeddingClient.embed(List.of("Hello", "World"));
            System.out.println("EMBEDDINGS SIZE: " + embeddings.size());

        };
    }


}
