# Spring AI Vertex AI PaLM2 AOT Demo Application

NOTE: Works only within US region. Use VPN if outside.

```
./mvnw clean install -DskipTests -Pnative native:compile
```

```
./target/vertexai-palm2-aot-demo
```

## Generate GraalVM configs

```
java -Dspring.aot.enabled=true \
    -agentlib:native-image-agent=config-output-dir=target/config-dir/ \
    -jar target/vertexai-palm2-aot-demo-0.0.1-SNAPSHOT.jar
```