# Kafka Topics & Partitions

## What is a Kafka Topic?

A **topic** is like a category or channel in Kafka.

* Producers **send messages** to a topic.
* Consumers **read messages** from a topic.

Think of a topic like a **mailbox**:

* Producers are people **dropping letters** into the mailbox.
* Consumers are people **collecting the letters**.
* Everyone agrees on the mailbox name (the topic name).

Topics help organize data.

* Example:

  * `orders` → messages about customer orders
  * `payments` → messages about payments
  * `shipments` → messages about deliveries

---

## How Do Partitions Work?

Each topic is **split into partitions**.

* A partition is an **ordered log** (a sequence of messages).
* Messages are always **appended to the end**.
* Every partition has a **leader broker** that handles reads and writes.

Why partitions?

* They let Kafka **scale**: many partitions can be spread across different brokers.
* They allow **parallelism**: multiple consumers can read different partitions at the same time.

🔑 **Key idea**: When a producer sends a message, a **key** can decide which partition it goes to.

* Same key → always goes to the same partition.
* Useful for grouping related messages (e.g., all orders from the same customer).

👉 Simple picture:

- Topic = a big box.

- Partitions = smaller boxes inside the big box.

- Messages = papers stored inside the small boxes.

This way, one big topic can be split into many smaller parts, which makes it easier to store and read messages quickly

---

## What Are Offsets?

Each message inside a partition has an **offset**.

* The offset is a **number** that shows the position of the message in that partition.
* Offsets start at 0 and increase one by one.

Offsets are important because:

* They let consumers know **where they left off**.
* A consumer can restart and **resume from the last offset** it read.

---

## Ordering in Kafka

Kafka **keeps messages in order within a single partition**.

* If you send messages `A → B → C` to the same partition, consumers will always read them in that order.
* Across multiple partitions, there is **no global order** — only order inside each partition.

This means:

* If you care about strict ordering, use **one partition**.
* If you need high throughput and can relax ordering, use **many partitions**.

---

## Quick Terminology

| Term          | What It Means                                                              |
| ------------- | -------------------------------------------------------------------------- |
| **Topic**     | A named category for messages.                                             |
| **Partition** | A split of a topic; an ordered log stored on brokers.                      |
| **Offset**    | A unique ID for a message inside a partition.                              |
| **Ordering**  | Kafka guarantees order **only within a partition**, not across partitions. |

---

## 🐳 Creating a Topic with Partitions

After starting a Kafka cluster, you can create a topic with multiple partitions:

```bash
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --create \
  --topic orders \
  --partitions 3 \
  --replication-factor 3 \
  --bootstrap-server broker-1:9092
```

This command creates a topic named `orders` with **3 partitions** and **replication factor 3**.

---

## 🔍 Inspecting a Topic

To check the details of the topic:

```bash
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --describe \
  --topic orders \
  --bootstrap-server broker-1:9092
```

Example output:

```yaml
Topic: orders      TopicId: rQKRV4sqTJW-GoDUfnJGKg PartitionCount: 3       ReplicationFactor: 3    Configs: 
  Topic: orders      Partition: 0    Leader: 3       Replicas: 3,1,2 Isr: 3,1,2      Elr:    LastKnownElr: 
  Topic: orders      Partition: 1    Leader: 1       Replicas: 1,2,3 Isr: 1,2,3      Elr:    LastKnownElr: 
  Topic: orders      Partition: 2    Leader: 2       Replicas: 2,3,1 Isr: 2,3,1      Elr:    LastKnownElr: 
```

Here:

* The topic `orders` has **3 partitions**.
* Each partition has a **leader broker** and replica brokers.
* `Isr` = In-Sync Replicas (the brokers that have the latest copy).

---

✅ With this knowledge, you now understand **what topics are**, **how partitions scale Kafka**, and **how offsets and ordering work** to keep track of messages.
