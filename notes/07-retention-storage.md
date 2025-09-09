# Retention & Storage

This section explains how logs are kept, cleaned, and stored on disk. It also shows how data is organized and managed for better performance.

Before going in deep, let's explain some confusing terms in kafka storage context. In other words, the relationship Between Log, Message, and Data.

- Data / Message: The smallest unit of information. For example, a user action like login or logout.

- Log: A collection of messages stored in order. Logs keep track of all messages over time.

Example:

> Message 1: user1 -> login  
> Message 2: user1 -> logout  
> Message 3: user2 -> login

All three messages together form a log.

---

## Log Retention Policy
- What it means: Retention policy tells how long the system will keep old logs.

- Why it matters: Keeping all logs forever will fill up storage. A policy helps to delete or move old data.

- How it works:

    - Logs can be kept for a set time (default: 7 days).

    - Logs can be kept until they reach a certain size (default: 1GB).

    - After that, old logs are removed or compacted.

---

## Log Compaction

- What it means: Compaction keeps only the latest value for each key in the log.

- Why it matters:

    - It saves space.

    - It makes sure the log has the most recent data.

***Example:***
 - Messages in the log:
    > user1 -> login  
    > user1 -> logout  
    > user1 -> login

 - After compaction, only the last message remains:
    > user1 -> login  

---

## Storage on disk (segments, indexes)

Logs are stored on disk in a structured way. This makes it fast to read and write messages.

 - Segments:

    - Logs are split into smaller files called segments.

    - Each segment has a fixed size (e.g., 1GB).

    - When a segment is full, a new one is created.

 - Indexes:

    - Index files help to quickly find a message inside a segment.

    - They point to the exact location of a record.

**Relationship Summary:**

> Data / Message → Stored in Logs → Logs divided into Segments → Segments indexed on Disk

---

## Demonstrations

### 1. Messages

**Produce some messages:**

```bash
    kafka-console-producer.sh --bootstrap-server localhost:9092 --topic my-topic
    > user1:login
    > user1:logout
    > user2:login
```

- Each line entered is a message.

- Messages are stored in order inside a log.

**Consume messages to verify:**

```bash
    kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-topic    --from-beginning
```

### 2. Logs

Kafka stores logs on disk under log directory configured in file server.properties.

**Check log directory:**

```bash
    grep "log.dir" ~/kafka_2.13-4.0.0/config/server.properties 
    
    # output: log.dirs=/tmp/kraft-combined-logs

    cd /tmp/kraft-combined-logs/

    ls -d *topic*
```

Example output:

> my-topic-0  test-topic-0


### 3. Segments

Logs are split into smaller files called **segments** for easier management.

**Check segment files:**

```bash
    ls /tmp/kraft-combined-logs/my-topic-0/*.log
```

> 00000000000000000000.log  
> 00000000000000000001.log

- Each .log file is a segment containing messages.

- New segments are created when previous ones reach a certain size.


### 4. Indexes

Kafka uses index files to quickly find messages inside a segment.

Check index files:

```bash 
    ls /tmp/kraft-combined-logs/my-topic-0/*.index
```


Example output:

> 00000000000000000000.index  
> 00000000000000000001.index

- Each index corresponds to a segment file.

- Maps message offsets → byte positions for fast access.

Check offset - physical position mapping : 

```bash
    kafka-run-class.sh kafka.tools.DumpLogSegments --files /tmp/kraft-combined-logs/my-topic-0/00000000000000000000.log
```

Look for "lastOffset" and "position" values.

### 5. Retention

Retention controls how long Kafka keeps log segments.

**Check current retention settings:**

```bash
    kafka-configs.sh --bootstrap-server localhost:9092 --entity-type topics --entity-name my-topic --describe
```  

Example output:

> Dynamic configs for topic my-topic are:

**Change retention to 1 day:**

```bash
    kafka-configs.sh --bootstrap-server localhost:9092 --entity-type topics --entity-name my-topic --alter --add-config retention.ms=86400000
``` 

- Check again the retention settings :

Example output:

> Dynamic configs for topic my-topic are:  
>> retention.ms=86400000 sensitive=false synonyms={DYNAMIC_TOPIC_CONFIG:retention.ms=86400000}


- After 1 day, old segment files are deleted automatically from the log directory.


### 6. Compaction

Compaction allows to keep only the latest message per key, removing older duplicates.

**Check current cleanup policy:**

```bash
    kafka-configs.sh --bootstrap-server localhost:9092 --entity-type topics --entity-name my-topic --describe
```

Example output:

> Dynamic configs for topic my-topic are:
>> retention.ms=86400000 sensitive=false synonyms={DYNAMIC_TOPIC_CONFIG:retention.ms=86400000}

**Enable log compaction:**

```bash
    kafka-configs.sh --bootstrap-server localhost:9092 --entity-type topics --entity-name my-topic --alter --add-config cleanup.policy=compact
```

- Check agian cleanup policy to see the change
Example output:

> Dynamic configs for topic my-topic are:  
>> cleanup.policy=compact sensitive=false synonyms={DYNAMIC_TOPIC_CONFIG:cleanup.policy=compact, DEFAULT_CONFIG:log.cleanup.policy=delete}  
>> retention.ms=86400000 sensitive=false synonyms={DYNAMIC_TOPIC_CONFIG:retention.ms=86400000}

- Messages with the same key are compacted so only the latest remains.

---

## Summary

- A message is the smallest unit of data in Kafka.

- Log is a sequence of messages.

- Retention controls how long logs stay.

- Compaction reduces old duplicates.

- Storage uses segments and indexes to organize logs efficiently.
