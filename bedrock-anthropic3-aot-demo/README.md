# Spring AI OpenAI AOT Demo Application

```
./mvnw clean install -DskipTests -Pnative native:compile
```

```
./target/bedrock-anthropic3-aot-demo
```

## Generate GraalVM configs

```
java -Dspring.aot.enabled=true \
    -agentlib:native-image-agent=config-output-dir=target/config-dir/ \
    -jar target/bedrock-anthropic3-aot-demo-0.0.1-SNAPSHOT.jar
```