# Kafka At-Most-Once Example

This project demonstrates at-most-once delivery semantics in Apache Kafka using a plain Java client.
It shows how to configure a producer and a consumer so that messages are never duplicated, though some messages may be lost.  

---  

## Overview

- Producer:

    - Sends messages with no acknowledgment (acks=0) and no retries (retries=0).
    
    - Idempotence disabled (enable.idempotence=false).
    
    - Messages include unique timestamps with nanosecond precision to distinguish each message.

- Consumer:

  - Uses manual commit (enable.auto.commit=false) 

  - Checks Kafka regularly for messages (poll(Duration.ofMillis(100))).

  - Offsets are committed before processing, ensuring at-most-once behavior.

- Topic: sensor-data

    - Messages represent simulated sensor readings with timestamps.

---  

## Maven Setup

- **Kafka client:** org.apache.kafka:kafka-clients:3.9.1

- **SLF4J logging:** org.slf4j:slf4j-simple:1.7.36 (compatible with Kafka 3.x)

- **Java version:** 21 (compile and target)  

> Make sure your Maven `pom.xml` includes these dependencies and compiler settings.  

---  

## Running the Example

1. Ensure a Kafka broker is running locally (localhost:9092).

2. Create the topic sensor-data (or enable auto-create).

3. Build and run the project using Maven:

    ```bash
        mvn clean compile exec:java -Dexec.mainClass=AtMostOnceExample
    ```

4. Observe console output:

   - Sent messages include timestamps with nanosecond precision.

   - Consumer prints messages as they are received.

   - **Stop the application** before all messages are processed and **restart the app**.

> Note: Some messages may be lost due to at-most-once semantics, but no duplicates will occur.

---  
## Key Concepts

- **At-Most-Once Delivery:** Messages are never delivered more than once, but some messages may be lost.

- **Consumer Polling:** Regular calls to poll() are necessary to receive messages and maintain consumer group membership.

- **Timestamps:** Including nanoseconds ensures unique message values, useful for testing or demos.

- **Auto Offset Commit:** Offsets are committed manually before processing messages, aligning with at-most-once semantics.

---  
## Notes for Handbook Readers

- This project is a **minimal, plain Java Kafka example**, suitable for testing and demonstration.

- For production use, consider **handling failures, logging, and graceful shutdown.**

- Can be used as a reference when explaining **Kafka delivery semantics** in tutorials or workshops.

- But using Spring Boot is suitable for high level development.
