# Kafka Broker & Controller

## What is a Kafka Broker?

A **Kafka broker** is a server in a Kafka system.  
It has three main jobs:  
- **Store data** in topics and partitions.  
- **Serve clients** (producers and consumers) when they send or read data.  
- **Talk to other brokers** to keep the system running smoothly.  

Think of a broker like a **warehouse**:  
- Producers **deliver packages** (messages) to the warehouse.  
- Consumers **pick up packages** when they need them.  
- The warehouse (broker) **keeps everything safe and organized**.  

---

## Role of the Controller

In every Kafka cluster, one broker is chosen as the **controller**.  
The controller has special responsibilities:  
- **Manage partitions**: Decides which broker is the “leader” for each partition.  
- **Handle broker failures**: If a broker goes down, the controller makes sure another broker takes over quickly.  
- **Coordinate the cluster**: Keeps track of all brokers, assigns partition leaders, and ensures every broker knows its role.  

You can think of the controller as the **team leader** of all brokers.  
- Brokers = workers in a warehouse.  
- Controller = manager who assigns tasks and handles problems.  

---

## Kafka Cluster Overview

A **Kafka cluster** is a group of brokers working together.  
- Usually, there are **multiple brokers** (3, 5, or more).  
- Data is **replicated** (copied) across brokers for safety.  
- One broker is the **controller**, the rest are **regular brokers**.  

### Example setup:  
- **3 Brokers** → store and serve data.  
- **1 Controller (within those brokers)** → manages the cluster.  
- Producers send messages, and consumers read them.  

This design makes Kafka **reliable, scalable, and fault-tolerant**.  

---

## Quick Terminology

| Term       | What It Means |
|------------|----------------|
| **Broker** | A server that stores data and serves clients. |
| **Controller** | A special broker that manages the cluster and handles failures. |
| **Cluster** | A group of brokers working together to provide reliability and scalability. |

---

# 🐳 Creating a Kafka Cluster with Docker

We can set up a **3-broker Kafka cluster** using Docker Compose in **KRaft mode** (no ZooKeeper needed).  

### 1. Create `docker-compose.yml`

Create a docker compose file in a directory with this file : [docker-compose.yml](../examples/docker-compose.yml) 


### 2. Start the cluster

```bash 
    docker-compose up -d
```

### 3. Create a topic

Run inside any broker container (example: broker-2)
```bash
  docker exec -it broker-2 /opt/kafka/bin/kafka-topics.sh --create \
      --topic test-docker \
      --partitions 3 \
      --replication-factor 3 \
      --bootstrap-server broker-2:9092
```

---
# 🔍 Inspecting the Cluster

1. Find the Controller

Run inside any broker container (example: broker-1):

```bash
  docker exec -it broker-1 /opt/kafka/bin/kafka-metadata-quorum.sh \
  --bootstrap-server broker-1:9092 describe --status
```
This will show the LeaderId, which is the broker acting as the controller.

2. Check Partition Leaders
```bash
    docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --describe \
    --topic test-docker \
    --bootstrap-server broker-1:9092
```

Example output:
```sql
Topic: test-docker      TopicId: rQKRV4sqTJW-GoDUfnJGKg PartitionCount: 3       ReplicationFactor: 3    Configs: 
  Topic: test-docker      Partition: 0    Leader: 3       Replicas: 3,1,2 Isr: 3,1,2      Elr:    LastKnownElr: 
  Topic: test-docker      Partition: 1    Leader: 1       Replicas: 1,2,3 Isr: 1,2,3      Elr:    LastKnownElr: 
  Topic: test-docker      Partition: 2    Leader: 2       Replicas: 2,3,1 Isr: 2,3,1      Elr:    LastKnownElr: 
```

Here:

- **Partition 0 leader = Broker 3**

- **Partition 1 leader = Broker 1**

- **Partition 2 leader = Broker 2**

✅ With this Docker setup, you now have a real Kafka cluster (3 brokers), can identify the controller, and see which brokers are leaders for partitions.