# Spring AI Milvus AOT

```
./mvnw clean install -DskipTests -Pnative native:compile
```

```
./target/pgvector-demo
```

## Generate GraalVM configs

```
java -Dspring.aot.enabled=true \
    -agentlib:native-image-agent=config-output-dir=target/config-dir/ \
    -jar target/pgvector-demo-0.0.1-SNAPSHOT.jar
```


```
docker run -it --rm --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres ankane/pgvector
```