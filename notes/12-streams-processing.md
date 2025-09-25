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

Other stream processing tools include:

- **ksqlDB:** SQL-like queries for Kafka topics; good for quick, simple logic without coding.
- **Apache Flink:** Advanced, scalable stream and batch processing; best for complex or large workloads.
- **Spark Streaming:** Integrates with Spark for batch and micro-batch processing; ideal if you already use Spark.

**Choose:**
- **Kafka Streams** for simple, embedded Java stream processing.
- **ksqlDB** for easy, SQL-based transformations.
- **Flink** for complex, large-scale processing.
- **Spark Streaming** for unified batch and stream analytics with Spark.

---

Kafka Streams is **great for building real-time** applications. For more complex needs, consider alternatives like Flink or Spark Streaming.