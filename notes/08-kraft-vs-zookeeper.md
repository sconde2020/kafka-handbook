# KRaft vs ZooKeeper

Apache Kafka is a popular platform for streaming data. For a long time, Kafka depended on **ZooKeeper** to manage its metadata. But in recent years, Kafka has introduced a new way of handling metadata called **KRaft mode** (Kafka Raft). Let’s look at the differences, the history, and why this change happened.

---

## Kafka Metadata

### What is Metadata in Kafka?

In simple terms, **metadata** is "data about data."  
In Kafka, metadata includes important information such as:

- The list of brokers in the cluster
- Topics, partitions, and their leaders
- Replica assignments for partitions
- Access control lists (ACLs)
- Configuration settings

This metadata is critical because it allows Kafka producers, consumers, and brokers to know **where messages should go and how to coordinate**.

### Evolution of Kafka Metadata Management

- **2011**: First Kafka release, with ZooKeeper dependency.
- **2010s**: Kafka adoption grew, but ZooKeeper became a bottleneck for very large clusters.
- **April 2021 (Kafka 2.8.0)**: KRaft introduced in early access mode.
- **September 2022 (Kafka 3.3.0)**: KRaft declared **production-ready**.
- **Today (Kafka 3.x+)**: Both modes exist, but the community is moving towards **KRaft-only Kafka**. Future versions plan to **fully remove ZooKeeper support**.

---

## Zookeeper

### What is ZooKeeper?

ZooKeeper is a separate distributed system created by Apache in **2010** to manage configuration and coordination.  
Kafka (first released in **2011**) used ZooKeeper from the beginning to handle tasks like:

- Storing cluster metadata
- Running leader elections for brokers
- Keeping track of broker health

For many years, this design worked well. But as Kafka became the backbone of large-scale data platforms at companies like LinkedIn, Uber, and Netflix, ZooKeeper showed limitations.

### Problems with ZooKeeper in Kafka

1. **Extra dependency**: Running ZooKeeper clusters alongside Kafka increased complexity.
2. **Scaling issues**: ZooKeeper could become a bottleneck in very large deployments.
3. **Operational overhead**: Administrators had to monitor, upgrade, and secure two systems.
4. **Slower metadata operations**: Creating topics, partitions, or applying changes could be slow.

---

## Kafka Kraft

### What is KRaft?

KRaft (Kafka Raft) is a new way of managing Kafka metadata **without ZooKeeper**.  
Introduced as an experimental feature in **Kafka 2.8.0 (April 2021)**, and declared **production-ready in Kafka 3.3 (September 2022)**, KRaft is now the future of Kafka metadata management.

Instead of ZooKeeper, Kafka uses its own **Raft-based consensus protocol** to agree on cluster metadata.

### The KRaft Controller

- The **KRaft controller** is a special broker (or group of brokers) in the cluster.
- It is responsible for **storing and managing all cluster metadata**.
- It uses the **Raft protocol** to replicate metadata logs across controller nodes for fault tolerance.
- Other brokers read metadata from the controller to stay up to date.

This means metadata is now managed *inside Kafka itself*, with no external system required.

---

## Kraft or Zookeeper

### Differences Between KRaft and ZooKeeper

| Feature              | ZooKeeper-based Kafka         | KRaft-based Kafka                      |
|----------------------|-------------------------------|----------------------------------------|
| **Introduced**       | 2011 (Kafka 0.8 with ZooKeeper) | 2021 (Kafka 2.8, experimental)         |
| **Metadata storage** | Stored in ZooKeeper           | Stored inside Kafka (KRaft controller) |
| **Consensus**        | Managed by ZooKeeper          | Uses Kafka’s Raft protocol             |
| **Complexity**       | Two systems to run (Kafka + ZooKeeper) | Single system (Kafka only)            |
| **Scalability**      | Limited with very large clusters | Designed for massive scale            |
| **Operations**       | More moving parts to monitor  | Simpler cluster operations             |

### Why KRaft Matters

- **Simpler architecture**: No need to run ZooKeeper separately.
- **Better scalability**: Handles very large clusters more smoothly.
- **Improved reliability**: Raft-based consensus ensures safer metadata handling.
- **Future direction**: Apache Kafka is transitioning fully to KRaft mode

---

## Demonstrations

When running Kafka in **KRaft mode**, it’s useful to know:

1. Which broker is currently the **leader controller**
2. Where the **metadata log** is stored locally

**You need to create a cluster by using the demo paragraph in [Broker & Controller](notes/03-broker-controller.md) chapter.**

### Checking the Leader Controller

Since we are running Kafka inside **Docker containers** (not on a local host installation), we need to use `docker exec` to run Kafka CLI tools inside the broker container.

To check the current leader controller, run the following command:

```bash
  docker exec broker-1 /opt/kafka/bin/kafka-metadata-quorum.sh --bootstrap-server broker-1:9092 describe --status
```
Example output:

> ClusterId:              5L6g3nShT-eMCtK--X86sw  
> LeaderId:               1  
> LeaderEpoch:            1  
> HighWatermark:          1076  
> MaxFollowerLag:         0  
> CurrentVoters:         [{"id": 1, "directoryId": null, "endpoints": ["CONTROLLER://broker-1:9093"]}, {"id": 2, "directoryId": null, "endpoints": ["CONTROLLER://broker-2:9093"]}, {"id": 3, "directoryId": null, "endpoints": ["CONTROLLER://broker-3:9093"]}]  
> CurrentObservers:       []  

Here’s what each field means:

- **ClusterId** – Unique identifier of your Kafka cluster.  
- **LeaderId** – The broker ID currently acting as the **controller leader** (responsible for managing cluster metadata).  
- **LeaderEpoch** – Version counter that increases whenever the leader changes (useful for tracking re-elections).  
- **HighWatermark** – The highest committed metadata offset across the quorum. Think of it as the "last safe checkpoint" for metadata.  
- **MaxFollowerLag** – The maximum lag (in offsets) between the leader and any follower in the quorum. Ideally `0`.  
- **CurrentVoters** – Broker IDs that are **voting members** of the metadata quorum (usually all your controller brokers).  
- **CurrentObservers** – Broker IDs that observe the quorum but do not vote (typically empty unless using observers).  

### Where Metadata is Stored

In KRaft, metadata is stored in a local log directory on each controller broker.

By default, this directory is defined in your server.properties or in environment variables while using docker:

To check the directory, run the following commands : 

```bash
  docker exec broker-1 env | grep KAFKA_LOG_DIRS

  # Output : /var/lib/kafka/data

  docker exec broker-1 ls -la /var/lib/kafka/data/__cluster_metadata-0  
```

You’ll see log segment files (e.g., 00000000000000000000.log) similar to Kafka data logs, but these files represent metadata records, not user messages.

---
## Conclusion

ZooKeeper played an important role in making Kafka successful, but it also added complexity and scaling limits.  
With **KRaft and the KRaft controller**, Kafka has evolved into a **self-managed, simpler, and more scalable system**.

This shift marks a major milestone in the evolution of distributed streaming platforms, preparing Kafka for the next decade of massive data workloads.

