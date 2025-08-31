# Introduction To Kafka

## What is Apache Kafka?

Apache Kafka is a platform that helps different applications share information with each other in **real time**.  

With Kafka, applications can:  
- **Send data** (publish)  
- **Save data** safely for later use (store)  
- **Read data** whenever they need it (subscribe/consume)  

You can think of Kafka like a **post office for data**:  
- Applications that **send messages** are like people mailing letters.  
- Kafka is the **post office** that sorts and stores all the letters.  
- Applications that **receive messages** are like people checking their mailbox.  

---

## Why Use Kafka?

- **Reliable**: Messages are saved on disk and copied to other servers so they are not lost.  
- **Scalable**: You can add more servers to handle more data.(**horizontal scaling**) 
- **Fast**: Can process streams of data with very small delays.  
- **Flexible**: Many different apps can read the same data in their own way.  

---

## Core terminology

| Term              | What It Means |
|-------------------|----------------|
| **Topic**         | A named category where data is stored and shared. |
| **Partition**     | A topic is divided into smaller, ordered parts (called partitions) so that many workers can process the data at the same time.” |
| **Producer**      | Application that sends data to Kafka topics. |
| **Consumer**      | Application that reads data from Kafka topics. |
| **Broker**        | Server that stores data and serves clients. |
| **Consumer Group**| A group of consumers that share the work of reading from a topic. |

---

## Quick Example

1. One application sends order events:  

```bash
kafka-console-producer.sh --topic orders --bootstrap-server localhost:9092
```

2. Another application can consume these events:
```bash
kafka-console-consumer.sh --topic orders --bootstrap-server localhost:9092 --from-beginning
```