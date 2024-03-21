package org.springframework.ai.aot.test.milvus;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MilvusVectorStoreApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MilvusVectorStoreApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);

	}

	@Bean
	ApplicationRunner applicationRunner(VectorStore vectorStore, EmbeddingClient embeddingClient) {
		return args -> {
			List<Document> documents = List.of(
					new Document(
							"Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!",
							Map.of("meta1", "meta1")),
					new Document("The World is Big and Salvation Lurks Around the Corner"),
					new Document("You walk forward facing the past and you turn back toward the future.",
							Map.of("meta2", "meta2")));

			// Add the documents to PGVector
			vectorStore.add(documents);

			Thread.sleep(5000);
			// Retrieve documents similar to a query
			List<Document> results = vectorStore.similaritySearch(SearchRequest.query("Spring").withTopK(5));
			System.out.println("RESULTS: " + results);};
	}

}
