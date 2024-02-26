package org.springframework.ai.aot.test.transformers.onnx;

import java.util.List;

import org.springframework.ai.transformers.TransformersEmbeddingClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TransformersOnnxAotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TransformersOnnxAotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(TransformersEmbeddingClient embeddingClient) {

        return args -> {
            System.out.println("EmbeddingClient: " + embeddingClient.getClass().getName());

            // Embedding Client
            List<List<Double>> embeddings = embeddingClient.embed(List.of("Hello", "World"));
            System.out.println("EMBEDDINGS SIZE: " + embeddings.size());

        };
    }
}
