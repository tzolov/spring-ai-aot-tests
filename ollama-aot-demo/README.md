# Spring AI OpenAI AOT Demo Application

```
./mvnw clean install -DskipTests -Pnative native:compile
```

```
./target/ollama-aot-demo
```

## Generate GraalVM configs

```
java -Dspring.aot.enabled=true \
    -agentlib:native-image-agent=config-output-dir=target/config-dir/ \
    -jar target/ollama-aot-demo-0.0.1-SNAPSHOT.jar
```

## Run Ollama and pull Mistral

```
cd test/resources

docker-compose -f ./docker-compose.yml up

curl -X POST http://locahost:11434/api/pull -d '{"name": "mistral"}'
```