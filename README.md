# 📘 Kafka Handbook

Welcome to the **Kafka Handbook** 🚀  
This repository contains explanations, examples and real projects about **Apache Kafka**.  
It is written in simple language with clear examples so anyone can learn — from beginners to advanced users.  

---

## 📂 Table of Contents

### 🟢 Beginner

#### 1. [Introduction](notes/01-introduction.md)  
- What is Kafka?  
- Why use Kafka?  
- Core terminology  

#### 2. [Setup](notes/02-setup.md)  
- Kafka with Zookeeper  
- Kafka with KRaft  
- Kafka with Docker  

#### 3. [Broker & Controller](notes/03-broker-controller.md)  
- What is a Kafka broker?  
- Role of the controller  
- Kafka cluster overview  
- Creating a Kafka Cluster with Docker  

#### 4. [Topics & Partitions](notes/04-topics-partitions.md)  
- What are topics?  
- How Do Partitions Work?  
- What Are Offsets?  
- Ordering in Kafka
- Quick Terminology
- Creating a Topic with Partitions

#### 5. [Producers](notes/05-producers.md)  
- How producers send messages  
- Partitioners & batching  
- Message acknowledgements  
- Demonstrations

#### 6. [Consumers & Consumer Groups](notes/06-consumers.md)  
- How consumers read messages  
- Consumer groups and parallelism  
- Consumer Group Coordinator
- Offset management  
- Demonstrations

#### 7. [Retention & Storage](notes/07-retention-storage.md)  
- Log retention policy  
- Log compaction  
- Storage on disk (segments, indexes)
- Demonstrations

#### 8. [KRaft vs ZooKeeper](notes/08-kraft-vs-zookeeper.md)  
- Kafka Metadata
- ZooKeeper
- Kafka Kraft
- Kraft or Zookeeper
- Demonstrations

---

### 🟡 Intermediate

#### 9. [Replication & Fault Tolerance](notes/09-replication-fault-tolerance.md)  
- Leader & Follower Replicas  
- In-Sync Replicas (ISR)  
- High availability in Kafka
- Demonstrations with Docker

#### 10. [Delivery Semantics](notes/10-delivery-semantics.md)  
- At-most-once  
- At-least-once  
- Exactly-once (EOS) 
- Examples with Java 

#### 11. [Security](notes/11-security.md)  
- Authentication (SASL, SSL)  
- Authorization (ACLs)  
- Encryption in transit 
- Common Kafka Security Configurations
- Example with Java

#### 12. [Kafka Streams & Processing](notes/12-streams-processing.md)  
- Kafka Streams API basics  
- State stores & windowing  
- Alternatives (ksqlDB, Flink, Spark Streaming)  

---

### 🔴 Advanced
#### 13. [Monitoring & Operations](notes/13-monitoring-ops.md)  
- Metrics (JMX, Prometheus, Grafana)  
- Log monitoring  
- Common troubleshooting  

#### 14. [Schema Management](notes/14-schema-management.md)  
- Why schemas matter (Avro, Protobuf, JSON)  
- Confluent Schema Registry  
- Backward/forward compatibility  

#### 15. [Performance & Tuning](notes/15-performance-tuning.md)  
- Producer configs (batch.size, linger.ms)  
- Consumer configs (fetch.min.bytes, max.poll.interval.ms)  
- Broker tuning & disk optimization  

#### 16. [Real-world Architectures](notes/16-architectures.md)  
- Kafka as a message queue vs. event streaming  
- Event-driven microservices  
- Data pipelines (Kafka + Spark/Flink + DWH)  

---

## 📊 Diagrams
See the [diagrams/](diagrams/) folder for visual explanations.  

---

## 📑 Cheatsheets
Quick references for commands and configs are in [cheatsheets/](cheatsheets/):  
- kafka-cli.md  
- offsets.md  
- producers.md  
- consumers.md  

---

## 🌍 Resources
Extra learning materials in [resources/](resources/):  
- books.md  
- blogs.md  
- official-docs.md  

---

## 🎯 Goal
The purpose of this repo is to act as a **handbook** — not just notes for myself, but also a learning resource for others who want to understand **Kafka step by step**.  

---
