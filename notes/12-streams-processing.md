# 12. Kafka Streams & Processing

This section explains how to process data streams using Apache Kafka. We will cover the basics of the Kafka Streams API, state stores and windowing, and look at some alternatives.

---

## Kafka Streams API Basics

Kafka Streams is a Java library for building applications that process data in real time. It reads data from Kafka topics, processes it, and writes results back to topics.

**Example:**  
Suppose you have a topic with user clicks. You want to count clicks per user.

```java
StreamsBuilder builder = new StreamsBuilder();
KStream<String, String> clicks = builder.stream("user-clicks");
KTable<String, Long> clickCounts = clicks
    .groupByKey()
    .count();
clickCounts.toStream().to("click-counts");
```

This code reads clicks, groups them by user, counts them, and writes the counts to another topic.

---

## State Stores & Windowing

**State stores** keep track of data across events, making it possible to maintain counts, aggregates, or join streams. This is essential for remembering previous information during stream processing.

**Windowing** groups data by time intervals, such as every minute or hour. It helps analyze trends and compute metrics within specific periods, making continuous streams easier to manage.

**Example:**  
Count clicks per user every 5 minutes, using state stores and windowing.

```java
clicks
    .groupByKey()
    .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
    .count(Materialized.as("user-click-counts-store"));
```

This code uses a state store to keep track of counts for each user within each 5-minute window. 

---

## Alternatives

There are other tools for stream processing, each with different strengths:

- **ksqlDB:** Provides a SQL-like interface for querying Kafka topics. It's ideal for users who prefer declarative queries and need to build simple stream processing logic quickly, without writing Java code.
- **Apache Flink:** Designed for advanced stream and batch processing. Flink excels at complex event processing, stateful computations, and large-scale data pipelines. Use Flink when you need high throughput, low latency, and sophisticated processing features.
- **Spark Streaming:** Part of Apache Spark, suitable for both batch and micro-batch stream processing. Spark Streaming is a good choice if you already use Spark for analytics and want to unify batch and streaming workloads.

**When to use which:**

- Use **Kafka Streams** for lightweight, embedded stream processing in Java applications, especially when you want tight integration with Kafka and need to deploy as part of your microservices.
- Use **ksqlDB** for rapid prototyping, simple transformations, and when your team prefers SQL over code.
- Use **Flink** for complex, large-scale, or stateful stream processing that requires advanced features and scalability.
- Use **Spark Streaming** if your data infrastructure is already built around Spark and you want to process both batch and streaming data together.

**Example with ksqlDB:**

```sql
CREATE TABLE click_counts AS
SELECT user_id, COUNT(*) FROM user_clicks
WINDOW TUMBLING (SIZE 5 MINUTES)
GROUP BY user_id;
```

---

Kafka Streams is great for building real-time applications. For more complex needs, consider alternatives like Flink or Spark Streaming.