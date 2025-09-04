# Producers

This section explains how Kafka **producers** work.  
A **producer** is any program or service that sends messages to Kafka topics. Producers are the source of data in a Kafka system.

---

## How Producers Send Messages

Producers create messages and send them to Kafka topics.  
Each message has:

- **Key** (optional): Used to determine which partition the message goes to.  
- **Value**: The actual content of the message.  
- **Metadata** (optional): Extra information, like timestamp.

The producer connects to a Kafka cluster and chooses which topic to send the message to.  
For example, if you have a topic called `orders`, a producer might send messages like:

```yaml
    order123: {"customer":"Alice","total":50.0}
    order124: {"customer":"Bob","total":30.0}
```

Here, `order123` and `order124` are the keys, and the JSON strings are the values.

---

## Partitioners & Batching

Kafka topics are split into **partitions**. Each partition is an ordered sequence of messages.  

- A **partitioner** decides which partition a message goes to.  
  - If a message has a key, Kafka will usually send all messages with the same key to the same partition.  
  - If there is no key, Kafka may choose a partition randomly or use a round-robin method.

- **Batching** improves performance. Instead of sending messages one by one, the producer can collect many messages and send them together as a batch.  
  - This reduces network overhead and increases throughput.  
  - Example: Sending 100 messages in one batch is faster than sending 100 messages individually.

---

## Message Acknowledgements

When a producer sends a message, it can wait for an **acknowledgement** from Kafka.  
Acknowledgements tell the producer whether the message was successfully received.  

- **acks=0**: The producer does not wait for any confirmation. Fastest, but messages might be lost.  
- **acks=1**: The producer waits for the **leader broker** of the partition to confirm receipt. Safer than `acks=0`.  
- **acks=all**: The producer waits for all **in-sync replicas** to confirm. This is the safest option, ensuring the message is replicated.

Choosing the right acknowledgement level is a trade-off between **reliability** and **performance**.  

- `acks=0` → very fast, less reliable  
- `acks=1` → balanced speed and safety  
- `acks=all` → safest, slower

---

## Demonstrations : 

This following examples demonstrate using Kafka console producer and consumer with a **local Kafka installation**.

### 1. Setting Up `KAFKA_HOME` in Your PATH

First, export the `KAFKA_HOME` environment variable to point to your Kafka installation directory.  
For example, if Kafka is installed in `/home/kafka/kafka_2.13-4.0.0`:

```bash
    export KAFKA_HOME=/home/kafka/kafka_2.13-4.0.0
    export PATH=$KAFKA_HOME/bin:$PATH
```

To make this permanent, add the above lines to your shell config file (~/.bashrc, ~/.zshrc, or ~/.profile) and reload it:

```bash
    source ~/.bashrc
```

Once done, you can run Kafka commands directly without prefixing them with <KAFKA_HOME>/bin/.

--- 

### 2. Sending Messages with the Kafka Console Producer

Send messages to a topic named test-topic:

```bash
    kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test-topic
```

Type your messages and press Enter to send each one.
Exit safely with Ctrl+D to flush messages.

--- 

### 3. Sending Messages with Keys

Create a topic named topic-with-keys with two partitions:

```bash
    kafka-topics.sh --create \
        --bootstrap-server localhost:9092 \
        --replication-factor 1 \
        --partitions 2 \
        --topic topic-with-keys
```

Send messages with keys using the key:value format:

```bash
    kafka-console-producer.sh \
        --bootstrap-server localhost:9092 \
        --topic topic-with-keys \
        --property "parse.key=true" \
        --property "key.separator=:"
```

Example messages:

```yaml
    user1:Hello, Kafka!
    user1:Message 1
    user2:Message 2
```

Viewing Which Partition Received the Message

Consume messages and display key, value, and partition:

```bash
    kafka-console-consumer.sh \
        --bootstrap-server localhost:9092 \
        --topic topic-with-keys \
        --from-beginning \
        --property print.key=true \
        --property print.value=true \
        --property print.partition=true
```

Example output:

```yaml
    Partition:0    user1    Hello, Kafka!
    Partition:0    user1    Message 1
    Partition:1    user2    Message 2
```

Messages with the same key always go to the same partition.

--- 

### 4. Demonstrating Acknowledgement Settings

Control how the producer waits for acknowledgements using the acks parameter:

No acknowledgement (acks=0) — fastest, but no delivery guarantees:

```bash
    kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test-topic --producer-property acks=0
```

Leader acknowledgement (acks=1) — default, balances speed and safety:

```bash
    kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test-topic --producer-property acks=1
```

All in-sync replicas (acks=all) — safest, ensures replication before acknowledging

```bash
    kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test-topic --producer-property acks=all
```

--- 

### 5. Deleting a Topic

Ensure your broker config (server.properties) has:

```yaml
    delete.topic.enable=true
```

Then delete the topic:

```bash
    kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic topic-with-keys
```

--- 

### 6. Notes

Always exit the producer with Ctrl+D to flush pending messages.

Verify topics exist before producing or consuming:

```bash
    kafka-topics.sh --list --bootstrap-server localhost:9092
```

Describe a topic:

```bash
    kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic topic-with-keys
```