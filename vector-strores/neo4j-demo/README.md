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


```
docker run --entrypoint /custom/entrypoint.sh -e NEO4J_ACCEPT_LICENSE_AGREEMENT=yes \
           -v <path/to/custom/entrypoint>:/custom \
           -v </path/to/local/neo4j/home>:/var/lib/neo4j \
           -p "8884:2004" \
           -p "9500:9500" \
       neo4j/neo4j:latest
```

```
docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    neo4j
```