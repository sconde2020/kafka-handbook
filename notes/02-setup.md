# ⚙️ Kafka Setup

This guide will help you set up **Apache Kafka** step by step.  
We’ll cover three approaches:  

- [**A. Kafka with Zookeeper (v3.7.2)**](#a-kafka-with-zookeeper-kafka372)  
- [**B. Kafka with KRaft (v4.0.0)**](#b-kafka-with-kraft-kafka400)  
- [**C. Kafka with Docker (apache/kafka:4.0.0 in KRaft mode)**](#c-kafka-with-docker-apachekafka400-in-kraft-mode)

---

### When to Use Each Setup Method

1. [**Kafka with Zookeeper (v3.7.2)**](#a-kafka-with-zookeeper-kafka372):  
    This setup is best for older systems or when you need compatibility with previous Kafka versions. Zookeeper is used to manage the cluster, and it’s a reliable choice for production environments.

2. [**Kafka with KRaft (v4.0.0)**](#b-kafka-with-kraft-kafka400):  
    Use this setup to take advantage of the latest Kafka features. KRaft (Kafka Raft) removes the need for Zookeeper, making deployment simpler. It’s ideal for new projects or when upgrading to Kafka 4.0.0 or later.

3. [**Kafka with Docker (KRaft mode)**](#c-kafka-with-docker-apachekafka400-in-kraft-mode):  
    This method is perfect for testing, development, or learning. Docker makes it easy to set up and remove Kafka without changing your local system.

4. **Beginner-Friendly Practice Setup**:  
    If you’re new to Kafka, start with the Docker setup because it’s simple. Then, try the Zookeeper and KRaft setups to understand their differences. This step-by-step approach will help you learn how Kafka works in different setups.

---

### System and Software Requirements

#### Kafka with Zookeeper (v3.7.2)
- **Operating System**: Linux, macOS, or Windows with WSL
- **Java**: JDK 8 or later
- **Network**: Open ports 2181 (Zookeeper) and 9092 (Kafka broker)

#### Kafka with KRaft (v4.0.0)
- **Operating System**: Linux, macOS, or Windows with WSL
- **Java**: JDK 11 or later
- **Network**: Open port 9092 (Kafka broker)

#### Kafka with Docker (apache/kafka:4.0.0 in KRaft mode)
- **Operating System**: Linux, macOS, or Windows with Docker installed
- **Docker**: Docker Engine 20.10 or later
- **Network**: Open ports 9092 and 9093
---

## A. Kafka with Zookeeper (kafka:3.7.2)

### 1. Download the package

```bash
    cd /opt   # or any other preferred folder you own
    wget https://downloads.apache.org/kafka/3.7.2/kafka_2.13-3.7.2.tgz
    tar -xzf kafka_2.13-3.7.2.tgz
```

### 2. Start environment

- Enter the new Kafka folder

```bash 
    cd /opt/kafka_2.13-3.7.2
```

- Start **Zookeeper**

```bash 
    bin/zookeeper-server-start.sh config/zookeeper.properties
```

- In a new terminal, start **kafka Broker**

```bash 
    bin/kafka-server-start.sh config/server.properties
```

### 3. Create Topics

- Create a topic 

```bash
    bin/kafka-topics.sh --create \
    --topic my-events \
    --bootstrap-server localhost:9092 \
    --partitions 1 \
    --replication-factor 1
``` 

- Verify topic creation

```bash 
    bin/kafka-topics.sh \
    --describe \
    --topic my-events \
    --bootstrap-server localhost:9093
```

### 4. Produce & Consume Message

- Producer : 

```bash
    bin/kafka-console-producer.sh \
    --topic my-events \
    --bootstrap-server localhost:9092
```
(Write some text and press Enter)

- In a new console, start the consumer:

```bash
    bin/kafka-console-consumer.sh --topic my-events \
        --bootstrap-server localhost:9092 \
        --from-beginning 
```

---

## B. Kafka with KRaft (kafka:4.0.0)

### 1. Download the package

```bash
    cd /opt   # or any other preferred folder you own
    wget https://downloads.apache.org/kafka/4.0.0/kafka_2.13-4.0.0.tgz
    tar -xzf kafka_2.13-4.0.0.tgz
```

### 2. Start environment

- Enter the new kafka folder

```bash 
    cd /opt/kafka_2.13-4.0.0
```

- Generate a cluster ID
    
```bash 
    KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
```

- Format storage 
```bash 
    bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/server.properties
```

- Start Kafka broker (KRaft mode)

```bash 
    bin/kafka-server-start.sh config/server.properties
```

### 3. Create Topics

- Create a topic 

```bash
    bin/kafka-topics.sh --create \
    --topic my-topic \
    --bootstrap-server localhost:9092 \
    --partitions 1 \
    --replication-factor 1
``` 

- Verify topic creation

```bash 
    bin/kafka-topics.sh \
    --describe \
    --topic my-topic \
    --bootstrap-server localhost:9093
```

### 4. Produce & Consume Message

- Producer : 

```bash
    bin/kafka-console-producer.sh \
    --topic my-topic \
    --bootstrap-server localhost:9092
```
(Write some text and press Enter)

- In a new console, start the consumer:

```bash
    bin/kafka-console-consumer.sh --topic my-topic \
        --bootstrap-server localhost:9092 \
        --from-beginning 
```

---

## C. Kafka with Docker (apache/kafka:4.0.0 in KRaft mode)

### 1. Download Docker image

```bash
    docker pull apache/kafka:4.0.0
```

### 2. Start environment

- Run Kafka in KRaft mode

```bash
    docker run -d \
        --name kafka-container \
        -p 9092:9092 \
        -p 9093:9093 \
        -e KAFKA_PROCESS_ROLES=broker,controller \
        -e KAFKA_NODE_ID=1 \
        -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
        -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 \
        -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
        -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT \
        -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
        apache/kafka:4.0.0
```

### 3. Create Topics

- Create a topic

```bash
    docker exec -it kafka-container \
        /opt/kafka/bin/kafka-topics.sh --create \
        --topic my-events \
        --bootstrap-server localhost:9092 \
        --partitions 1 \
        --replication-factor 1
```

- Verify topic creation
```bash
    docker exec -it kafka-container \
        /opt/kafka/bin/kafka-topics.sh --describe \
        --topic my-events \
        --bootstrap-server localhost:9092
```

### 4. Produce & Consume Message

- Start a producer to send messages to the topic

```bash
    docker exec -it kafka-container \
        /opt/kafka/bin/kafka-console-producer.sh \
        --topic my-events \
        --bootstrap-server localhost:9092
```
(Write some text and press Enter)

- Start a consumer to read messages from the topic

```bash
    docker exec -it kafka-container \
        /opt/kafka/bin/kafka-console-consumer.sh --topic my-events \
        --bootstrap-server localhost:9092 \
        --from-beginning
```

### 5. Stop kafka docker container

Stop internal kafka server then the container

```bash
    CONTAINER_NAME='kafka-container'

    docker ps | grep "$CONTAINER_NAME"

    docker exec $CONTAINER_NAME /opt/kafka/bin/kafka-server-stop.sh

    docker stop $CONTAINER_NAME

    docker rm $CONTAINER_NAME

    docker ps | grep "$CONTAINER_NAME"
```

