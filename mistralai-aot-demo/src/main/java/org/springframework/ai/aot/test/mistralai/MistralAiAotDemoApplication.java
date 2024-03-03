package org.springframework.ai.aot.test.mistralai;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatClient;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.MistralAiEmbeddingClient;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

@SpringBootApplication
public class MistralAiAotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MistralAiAotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(MistralAiChatClient chatClient, MistralAiEmbeddingClient embeddingClient) {
        return args -> {

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

            // Function calling.
            // Function name is the bean name. Only the Mistral AI large and small models support function calling.
            ChatResponse paymentStatusResponse = chatClient
                    .call(new Prompt("What's the status of my transaction with id T1005?",
                            MistralAiChatOptions.builder()
                                    .withModel(MistralAiApi.ChatModel.SMALL.getValue())
                                    .withFunction("retrievePaymentStatus").build()));

            System.out.println("PAYMENT STATUS: " + paymentStatusResponse.getResult().getOutput().getContent());
        };
    }

    public record Transaction(String transactionId) {
    }

    public record Status(String status) {
    }

    @Bean
    @Description("Get payment status of a transaction")
    public Function<Transaction, Status> retrievePaymentStatus() {
        return (transaction) -> new Status(PAYMENT_DATA.get(transaction).status());
    }

    // Assuming we have the following data
    public static final Map<Transaction, Status> PAYMENT_DATA = Map.of(
            new Transaction("T1001"), new Status("Paid"),
            new Transaction("T1002"), new Status("Unpaid"),
            new Transaction("T1003"), new Status("Paid"),
            new Transaction("T1004"), new Status("Paid"),
            new Transaction("T1005"), new Status("Pending"));
}
