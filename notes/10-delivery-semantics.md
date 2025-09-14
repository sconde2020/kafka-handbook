# 10. Delivery Semantics

In messaging systems like Apache Kafka, **delivery semantics** describe how messages are delivered between producers and consumers.  

There are three main types:

## 1. At-most-once

- **Definition:** Each message is delivered zero or one time.  
- **What it means:** Messages might be lost, but they are never delivered more than once.
- **Use case:** When occasional message loss is acceptable, but duplicates are not.
- **When this occurs:** 
    If the producer doesn't wait for broker acknowledgment and there is an issue (e.g., network issues or a crash), messages may be lost.

**Example:**  
Suppose you are sending sensor data where occasional loss is fine, but duplicates would cause confusion.  
```java
Properties props = new Properties();
props.put("acks", "0"); // No acknowledgment from broker
KafkaProducer<String, String> producer = new KafkaProducer<>(props);
// Send message without retries
producer.send(new ProducerRecord<>("sensor-data", "temperature:25"));
```
In this example, if the message is lost in transit, it will not be resent.

## 2. At-least-once

- **Definition:** Each message is delivered one or more times.  
- **What it means:** No messages are lost, but some might be delivered more than once (duplicates).
- **Use case:** When it is important that every message is processed, even if it means handling duplicates.
- **When this occurs:**  
    If the producer does not receive an acknowledgment from the broker (for example, due to a network issue or broker unavailability), it may retry sending the message. If the broker actually received and processed the original message but the acknowledgment was lost, the retry causes the same message to be delivered and processed again, resulting in duplicates.

**Example:**  
Imagine a logging system where every log entry must be recorded, even if some are duplicated.  
```java
Properties props = new Properties();
props.put("acks", "all"); // Wait for all replicas to acknowledge
props.put("retries", 3);  // Retry sending if needed
KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.send(new ProducerRecord<>("logs", "user logged in"));
```
Here, if a send fails because ack is not delivered, the producer retries, possibly causing duplicates.

## 3. Exactly-once (EOS)

- **Definition:** Each message is delivered only once, with no loss or duplication.
- **What it means:** Guarantees both no message loss and no duplicates.
- **Use case:** Critical systems where both loss and duplication are unacceptable (e.g., financial transactions).
- **When this occurs:**  
    This occurs when both the producer and consumer are configured for idempotence and transactions. The producer uses idempotent writes and transactional APIs, while the consumer processes messages within a transaction and commits offsets atomically. This ensures that even in the event of failures, messages are neither lost nor duplicated.

**Example:**  
For a banking application, transferring money must happen exactly once to avoid errors.  
```java
Properties props = new Properties();
props.put("acks", "all");
props.put("retries", Integer.MAX_VALUE);
props.put("enable.idempotence", "true"); // Enable idempotence, each message is tracked by a sequence number
props.put("transactional.id", "banking-app-1"); // Unique transactional ID
KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();
producer.beginTransaction();
producer.send(new ProducerRecord<>("transactions", "transfer $100"));
producer.commitTransaction();
```
This ensures the transaction is processed once and only once.

---

## Examples with Java

Below are plain java projects that demonstrate how to configure Kafka producers & consumers for each delivery semantic.

### [Kafka At-Most-Once Example](../examples/kafka-at-most-once-example)


### [Kafka At-Least-Once Example](../examples/kafka-at-least-once-example)


### [Kafka Exactly-Once Example](../examples/kafka-exactly-once-example)

---

## Summary:
- Use **at-most-once** when speed is more important than reliability.
- Use **at-least-once** when you must not lose messages, but can handle duplicates.
- Use **exactly-once** for the highest reliability, with no loss or duplication.
