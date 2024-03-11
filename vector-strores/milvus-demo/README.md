# Spring AI Milvus AOT

```
./mvnw clean install -DskipTests -Pnative native:compile
```

```
./target/azure-openai-aot-demo
```

## Generate GraalVM configs

```
java -Dspring.aot.enabled=true \
    -agentlib:native-image-agent=config-output-dir=target/config-dir/ \
    -jar target/milvus-demo-0.0.1-SNAPSHOT.jar
```