package org.springframework.ai.aot.test.vertexai.gemini;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.MediaData;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatClient;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

@SpringBootApplication
@ImportRuntimeHints(VertexAiGeminiAotDemoApplication.MockWeatherServiceRuntimeHints.class)
public class VertexAiGeminiAotDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(VertexAiGeminiAotDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    ApplicationRunner applicationRunner(VertexAiGeminiChatClient chatClient) {
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

            // Multi-modal query. NOTE: switch the model to "gemini-pro-vision"!
            byte[] data = new ClassPathResource("/vertex.test.png").getContentAsByteArray();
            var multiModalUserMessage = new UserMessage("Explain what do you see o this picture?",
                    List.of(new MediaData(MimeTypeUtils.IMAGE_PNG, data)));
            ChatResponse multiModalResponse = chatClient.call(new Prompt(List.of(multiModalUserMessage),
                    VertexAiGeminiChatOptions.builder().withModel("gemini-pro-vision").build()));
            System.out.println("MULTI-MODAL RESPONSE: " + multiModalResponse.getResult().getOutput().getContent());

            // Function calling
            var systemMessage = new SystemMessage("""
                    Use Multi-turn function calling.
                    Answer for all listed locations.
                    If the information was not fetched call the function again. Repeat at most 3 times.
                    """);
            UserMessage userMessage = new UserMessage(
                    "What's the weather like in San Francisco, in Paris and in Tokyo?");

            ChatResponse weatherResponse = chatClient.call(new Prompt(List.of(systemMessage, userMessage),
                    VertexAiGeminiChatOptions.builder().withFunction("weatherInfo").build()));
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

            hints.resources().registerResource(new ClassPathResource("/vertex.test.png"));
        }

    }

}
