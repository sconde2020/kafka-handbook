# Kafka Exactly-Once Example

This project demonstrates exactly-once delivery semantics in Apache Kafka using a plain Java client.
It shows how to configure a producer and a consumer so that messages are delivered **once and only once**, with no loss or duplication.

---

## Overview

- Producer:

    - Uses Kafka transactions (`transactional.id` configured) to ensure atomic writes.

    - Sends messages within a transaction and commits only after all messages are sent.

    - Requires acknowledgment (`acks=all`) and idempotence enabled (`enable.idempotence=true`).

- Consumer:

    - Uses manual commit (`enable.auto.commit=false`).

    - Reads messages with regular polling (`poll(Duration.ofMillis(100))`).

    - Processes messages and commits offsets **within the transaction**, ensuring exactly-once semantics.

- Topic: transactions

    - Messages represent simulated transactional events, each with a unique timestamp.

---

## Maven Setup

- **Kafka client:** org.apache.kafka:kafka-clients:3.9.1

- **SLF4J logging:** org.slf4j:slf4j-simple:1.7.36 (compatible with Kafka 3.x)

- **Java version:** 21 (compile and target)

> Make sure your Maven `pom.xml` includes these dependencies and compiler settings.

---

## Running the Example

1. Ensure a Kafka broker is running locally (localhost:9092).

2. Create the topic `transactions` (or enable auto-create).

3. Build and run the project using Maven:

```bash
    mvn clean compile exec:java -Dexec.mainClass=ExactlyOnceExample
```

4. Observe console output:

    - Sent messages include timestamps with nanosecond precision.

    - Consumer prints messages as they are received.

    - **Stop the application** before all messages are processed and **restart the app**.

> Note: With exactly-once semantics, messages are delivered once and only once—no duplicates, no loss.

---
## Key Concepts

- **Exactly-Once Delivery:** Guarantees that each message is delivered and processed only once.

- **Kafka Transactions:** Producers and consumers coordinate to ensure atomic writes and offset commits.

- **Idempotence:** Producer configuration prevents duplicate message delivery.

- **Manual Offset Commit in Transaction:** Offsets are committed as part of the transaction, aligning with exactly-once semantics.

---
## Notes for Handbook Readers

- This project is a **minimal, plain Java Kafka example**, suitable for testing and demonstration.

- For production use, consider **handling failures, logging, and graceful shutdown.**

- Can be used as a reference when explaining **Kafka delivery semantics** in tutorials or workshops.

- Using Spring Boot is suitable for high level development.

