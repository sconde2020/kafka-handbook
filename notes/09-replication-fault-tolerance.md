# Replication & Fault Tolerance

Apache Kafka is designed to handle large amounts of data reliably.  

One of its most powerful features is **replication**, which ensures **fault tolerance** and **high availability**.

This article explains how replication works in Kafka, the role of leaders and followers, what In-Sync Replicas (ISR) are, and how these concepts provide high availability. 

A **Docker-based demo** is included at the end.

---

## Leader & Follower Replicas

When you create a Kafka topic, it can have multiple **partitions**. Each partition is **replicated** across several brokers.

- One replica of each partition is chosen as the **leader**.
- The other replicas are called **followers**.

**How it works:**

- Producers send data **only to the leader** replica.
- Consumers read data **from the leader** by default.
- Followers copy the data from the leader to stay up to date.

This ensures that if the leader fails, a follower can quickly take over.

---

## In-Sync Replicas (ISR)

Not all replicas may always be up to date. That’s why Kafka keeps track of **In-Sync Replicas (ISR)**.

- An **ISR** is a set of replicas (leader + followers) that have fully caught up with the leader.
- If a follower falls behind for too long, it is removed from the ISR.
- Only replicas in the ISR are eligible to become the new leader if the current leader fails.

This guarantees that no committed data is lost when leadership changes.

---

## High Availability in Kafka

Kafka achieves **high availability** through replication and ISR:

1. **Replication factor**: You can configure how many copies of each partition exist. A common value is **3**.
2. **Leader election**: If the leader replica fails, Kafka automatically elects a new leader from the ISR.
3. **Durability**: As long as at least one replica in the ISR is alive, no committed data is lost.
4. **Fault tolerance**: With a replication factor of 3, Kafka can tolerate the failure of one broker (sometimes two, depending on settings).

---

## Demonstration with Docker

We will set up a **3-broker Kafka cluster** using Docker Compose.

### 1. Docker Compose Setup
Create a docker compose file in a directory with this file : [docker-compose.yml](../examples/docker-compose.yml) 

Then start the cluster:
```bash 
  docker-compose up -d
```

--- 

### 2. Create a Topic with Replication
Open a shell inside one broker (example with broker-1) and move to kafka bin folder inside the container:

```bash 
   docker exec -it broker-1 bash
```
```bash 
   cd /opt/kafka/bin/ 
```

Then run:
```bash 
  kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --topic demo-replication \
  --partitions 1 \
  --replication-factor 3
```

---

### 3. Describe the Topic
Still inside the broker container:
```bash 
  kafka-topics.sh --describe \
  --bootstrap-server broker-1:9092 \
  --topic demo-replication
```

Example output:
```sql
    Topic: demo-replication TopicId: UosPf9uJRwevr72EfgBXOg PartitionCount: 1       ReplicationFactor: 3    Configs: 
        Topic: demo-replication Partition: 0    Leader: 2       Replicas: 2,3,1 Isr: 2,3,1      Elr:    LastKnownElr:
``` 

- **Leader: 2** → Broker 2 is the leader  
- **Replicas: 2,3,1** → All three brokers store a copy  
- **Isr: 2,3,1** → All are in sync  

---

### 4. Simulate a Broker Failure
Stop broker-2:
```bash
    docker exec broker-2 kill 1
```
Then describe again from broker-1:
```bash
    docker exec broker-1 \
      /opt/kafka/bin/kafka-topics.sh --describe \
        --bootstrap-server localhost:9092 \
        --topic demo-replication
```
Example output:
```sql
Topic: demo-replication TopicId: UosPf9uJRwevr72EfgBXOg PartitionCount: 1       ReplicationFactor: 3    Configs: 
    Topic: demo-replication Partition: 0    Leader: 3       Replicas: 2,3,1 Isr: 3,1        Elr:    LastKnownElr: 
```
- **Leader switched to broker-3**
- Broker 2 dropped out of the ISR

---

### 5. Restart the Failed Broker
Bring broker-2 back:

```bash
    docker start broker-2
```
After a short while, check again:
```sql
Topic: demo-replication TopicId: UosPf9uJRwevr72EfgBXOg PartitionCount: 1       ReplicationFactor: 3    Configs: 
    Topic: demo-replication Partition: 0    Leader: 3       Replicas: 2,3,1 Isr: 3,1,2      Elr:    LastKnownElr: 
```
Broker 2 has rejoined the ISR, and the cluster is healthy again.

## Summary

- **Leaders and followers** keep partitions available.

- **In-Sync Replicas (ISR)** guarantee data safety.

- **High availability** is achieved by automatic leader election and replication.

- Using Docker makes it easy to simulate broker failures and observe how Kafka stays fault-tolerant.

