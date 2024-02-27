package org.springframework.ai.aot.test.azure.openai;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import reactor.core.publisher.Flux;

import org.springframework.ai.azure.openai.AzureOpenAiChatClient;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints(AzureOpenaiAotDemoApplication.MockWeatherServiceRuntimeHints.class)
public class AzureOpenaiAotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AzureOpenaiAotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(AzureOpenAiChatClient chatClient, AzureOpenAiEmbeddingClient embeddingClient) {
        return args -> {
            System.out.println("ChatClient: " + chatClient.getClass().getName());
            System.out.println("EmbeddingClient: " + embeddingClient.getClass().getName());

            // Synchronous Chat Client
            String response = chatClient.call("Tell me a joke.");
            System.out.println("SYNC RESPONSE: " + response);

            // Streaming Chat Client
            Flux<ChatResponse> flux = chatClient.stream(new Prompt("Tell me a joke."));
            String fluxResponse = flux.collectList().block().stream().map(r -> r.getResult().getOutput().getContent())
                    .collect(Collectors.joining());
            System.out.println("ASYNC RESPONSE: " + fluxResponse);

            // Embedding Client
            System.out.println(embeddingClient.embed("Hello, World!"));
            List<List<Double>> embeddings = embeddingClient.embed(List.of("Hello", "World"));
            System.out.println("EMBEDDINGS SIZE: " + embeddings.size());

            // Function calling
            ChatResponse weatherResponse = chatClient.call(new Prompt("What is the weather in Amsterdam, Netherlands?",
                    AzureOpenAiChatOptions.builder().withFunction("weatherInfo").build()));
            System.out.println("WEATHER RESPONSE: " + weatherResponse.getResult().getOutput().getContent());
        };
    }

    @Bean
    @Description("Get the weather in location")
    public Function<MockWeatherService.Request, MockWeatherService.Response> weatherInfo() {
        return new MockWeatherService();
    }

    public static class MockWeatherService
            implements Function<MockWeatherService.Request, MockWeatherService.Response> {

        @JsonInclude(Include.NON_NULL)
        @JsonClassDescription("Weather API request")
        public record Request(
                @JsonProperty(required = true, value = "location") @JsonPropertyDescription("The city and state e.g. San Francisco, CA") String location,
                @JsonProperty(required = true, value = "unit") @JsonPropertyDescription("Temperature unit") Unit unit) {
        }

        public enum Unit {
            C, F;
        }

        @JsonInclude(Include.NON_NULL)
        public record Response(double temperature, double feels_like, double temp_min, double temp_max, int pressure,
                int humidity, Unit unit) {
        }

        @Override
        public Response apply(Request request) {
            System.out.println("Weather request: " + request);
            return new Response(11, 15, 20, 2, 53, 45, Unit.C);
        }

    }

    public static class MockWeatherServiceRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register method for reflection
            var mcs = MemberCategory.values();
            hints.reflection().registerType(MockWeatherService.Request.class, mcs);
            hints.reflection().registerType(MockWeatherService.Response.class, mcs);
        }

    }

}
