# 15. Performance & Tuning

This tutorial will help you understand how to make Apache Kafka faster and more efficient. We will look at settings for producers, consumers, and brokers.

---

## Producer Configs

Producers send data to Kafka. You can change their settings to improve speed.
- **batch.size**  
    This sets how much data (in bytes) the producer collects before sending it.  
    *Tip:* Bigger batch size can make sending faster, but uses more memory.

- **linger.ms**  
    This sets how long the producer waits before sending a batch, even if it is not full.     
    *Tip:* A small wait time can help group messages together, making sending more efficient.

- **Example: Configuring Producer in Spring Boot**

    ```yaml
    # application.yml
    spring:
    kafka:
        producer:
        batch-size: 32768 # Default: `16384` (bytes)  
        linger-ms: 10 # Default: `0` (milliseconds)  
    ```

    Or in Java:

    ```java
    Map<String, Object> props = new HashMap<>();
    props.put("batch.size", 32768);
    props.put("linger.ms", 10);
    KafkaProducer<String, String> producer = new KafkaProducer<>(props);
    ```

---

## Consumer Configs

Consumers read data from Kafka. Their settings can help with speed and reliability.

- **fetch.min.bytes**  
    This sets the minimum amount of data the consumer waits for before reading.  
    *Tip:* A higher value means the consumer waits for more data, which can be faster but may add delay.

- **max.poll.interval.ms**  
    This sets the maximum time between polls.      
    *Tip:* If your consumer takes longer to process messages, increase this value to avoid that  
           the broker removes the consumer from the group and eventual errors.


 - **Example: Configuring Consumer in Spring Boot**

    ```yaml
    # application.yml
    spring:
    kafka:
        consumer:
        fetch-min-bytes: 1024 # Default: `1` (byte)  
        max-poll-interval-ms: 300000 # Default: `300000` (milliseconds, or 5 minutes)  
    ```

    Or in Java:

    ```java
    Map<String, Object> props = new HashMap<>();
    props.put("fetch.min.bytes", 1024);
    props.put("max.poll.interval.ms", 300000);
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    ```

---
## Broker Tuning & Disk Optimization

Brokers store and manage data. Good settings and disk management help performance.

- Use fast disks (SSD is better than HDD).
- Keep enough free disk space.
- Set `log.segment.bytes` to control file sizes. Smaller segments can help with faster recovery.
- Monitor disk usage and broker health.

- **Example: Set Segment Size in `server.properties`**

    ```properties
    # server.properties
    log.segment.bytes=134217728  # 128 MB segments
    ```
---

## Other Highly Impactful Kafka Tuning Properties

| Property                | Type      | Description                                                                 | Default Value      | Typical Tuning Tip                                      |
|-------------------------|-----------|-----------------------------------------------------------------------------|--------------------|---------------------------------------------------------|
| **compression.type**    | Producer  | Compresses messages to reduce network usage.                                | `none`             | Use `lz4` or `snappy` for better throughput and lower bandwidth. |
| **acks**                | Producer  | Controls how many broker acknowledgments are required for a write.           | `1`                | Set to `all` for stronger durability, `0` for max speed. |
| **retries**             | Producer  | Number of times to retry sending a message on failure.                       | `2147483647`       | Lower for faster error handling, higher for reliability. |
| **enable.idempotence**  | Producer  | Ensures exactly-once delivery for messages.                                 | `false`            | Enable for critical data to avoid duplicates.            |
| **auto.offset.reset**   | Consumer  | What to do when there is no initial offset in Kafka.                        | `latest`           | Use `earliest` for batch jobs, `latest` for streaming.   |
| **enable.auto.commit**  | Consumer  | Automatically commit offsets periodically.                                  | `true`             | Disable for manual offset control and precise processing.|
| **session.timeout.ms**  | Consumer  | Time to detect consumer failures.                                           | `10000`            | Increase for slow consumers, decrease for fast failover. |
| **num.network.threads** | Broker    | Number of threads for network requests.                                     | `3`                | Increase for high network traffic.                       |
| **num.io.threads**      | Broker    | Number of threads for disk I/O.                                             | `8`                | Tune up for heavy disk usage or many partitions.         |
| **log.retention.hours** | Broker    | How long to keep log data before deleting.                                  | `168`              | Lower for less disk usage, higher for longer retention.  |
| **log.cleaner.enable**  | Broker    | Enables log compaction for topics.                                          | `true`             | Enable for compacted topics, disable for pure append-only. |


## Summary

- Adjust producer and consumer configs for better speed and reliability, such as batch sizes, linger times, and poll intervals.
- Use fast disks (preferably SSDs), monitor broker health, and optimize disk usage for best broker performance.
- Tune key Kafka properties like compression, acknowledgments, retries, and offset management to match your workload.
- Always test configuration changes in a staging environment to find the best settings for your specific system and use case.
- Monitor metrics and logs regularly to catch bottlenecks and ensure stable, efficient Kafka operations.
