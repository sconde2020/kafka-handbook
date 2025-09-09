# Consumers & Consumer Groups in Kafka

When working with **Apache Kafka**, one of the most important concepts to understand is how **consumers** and **consumer groups** work. Let’s break this down step by step in simple words.

---

## How Consumers Read Messages

A **consumer** is an application that reads data from a **Kafka topic.**

- A topic is like a folder where messages are stored.

- Messages inside a topic are divided into partitions.

Each consumer keeps track of what it has read by using something called an **offset**.

- The offset is just a number that tells “where the consumer stopped reading last time.”

- Example: If a topic partition has 100 messages and the consumer has read up to message 50, then its offset is **50**.

--- 

## Consumer Groups and Parallelism

Kafka allows multiple consumers to work together as a consumer group.

- A consumer group is a collection of consumers that share the work of reading data from a topic.

- Each partition is read by only one consumer in a group.

- If the topic has many partitions, the consumers will split them among themselves.*

This creates parallelism (faster processing).

- Example: If a topic has 4 partitions and there are 2 consumers in the group, then each consumer will read 2 partitions.

If a new consumer joins, Kafka will rebalance and divide the partitions again.

---

## Consumer Group Coordinator

In Kafka, the component that **coordinates consumer group reading** is the **Group Coordinator**.

Here’s how it works in simple terms:

- When a **consumer group** is created, one of the Kafka brokers takes the role of the Group Coordinator for that group.

- The Group Coordinator is responsible for:  

  1. **Tracking consumer group membership** (which consumers are part of the group).

  2. **Assigning partitions** to consumers (deciding who reads what).

  3. **Rebalancing** when a consumer joins, leaves, or crashes.

  4. **Managing committed offsets** so that consumers can continue where they left off.

So, the **Group Coordinator (on a Kafka broker)** is the key piece that makes sure partitions are divided correctly among consumers in a group.

---

## Offset Management

Offsets are very important in Kafka because they control where a consumer continues reading from.

- **Automatic offset commit:** Kafka automatically saves the offset after the consumer reads messages.

- **Manual offset commit:** Developers can manually control when to save the offset. This is useful when you want to be sure the message has been fully processed before marking it as “done.”

Example:

- If you are saving messages to a database, you may want to commit the offset after the message is stored.

- This way, if your app crashes, Kafka will resend the last uncommitted messages, and no data will be lost.

---

## Demonstrations

### 1. Start a consumer group
```bash
    kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic test-topic --group my-group
```

### 2. Send messages to the topic
```bash
    kafka-console-producer.sh --bootstrap-server localhost:9092 \
    --topic test-topic
```

### 3. Add another consumer to the same group
```bash
    kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic test-topic --group my-group
```

### 4. Check the Group Members and Coordinator
```bash
    kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
    --describe --group my-group --state
```

***Example Output***
```sql
    GROUP           COORDINATOR (ID)          ASSIGNMENT-STRATEGY  STATE                #MEMBERS
    my-group        127.0.0.1:9092  (1)       range                Stable               2
```

### 5. Check offsets
```bash
    kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
    --describe --group my-group
```

***Example Output***
```sql
    GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                           HOST            CLIENT-ID
    my-group        test-topic      0          3               3               0               console-consumer-61a606db-3b42-4b4b-a3de-67f2f035fca6 /127.0.0.1      console-consumer
```

Let’s break this output down in simple terms :

***CURRENT-OFFSET***
  - The last offset that the consumer group has committed (i.e., "the position where the consumer says it has finished reading").  
  - This is basically: "I’ve read messages up to here."

***LOG-END-OFFSET***  
    - The latest offset available in the partition (the head of the log).  
    - This tells you: "The most recent message written is at this offset."

***LAG***  
    - This shows how many messages are still waiting to be consumed.  
    - The difference between LOG-END-OFFSET and CURRENT-OFFSET.  

```sql
    LAG = LOG-END-OFFSET - CURRENT-OFFSET
```  

---

## Summary

- Consumers read messages from topics.

- Consumer groups allow multiple consumers to share the work.

- The Group Coordinator (a Kafka broker) manages consumer groups by keeping track of members, assigning partitions, handling rebalances, and storing committed offsets.

- Offsets track the reading position and can be managed automatically or manually.

- With a local Kafka setup, you can easily try these commands to see consumer groups, group coordination, and offset management in action.
