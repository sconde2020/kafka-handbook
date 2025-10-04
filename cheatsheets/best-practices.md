# Kafka Best Practices Cheatsheet

## Producer Configs
```properties
# Enable idempotence
enable.idempotence=true

# Batching
batch.size=32768
linger.ms=20

# Compression
compression.type=snappy

# Retries & Backoff
retries=5
retry.backoff.ms=100

# Timeouts
request.timeout.ms=30000

# Strong durability
acks=all
```

## Consumer Configs
```properties
# Consumer group
group.id=my-consumer-group

# Manual offset commit
enable.auto.commit=false

# Error handling & backpressure
max.poll.records=500
max.poll.interval.ms=300000
```

## Topic & Partition CLI
```sh
# Create topic with partitions and replication
kafka-topics.sh --create --topic my-topic --partitions 6 --replication-factor 3 --bootstrap-server localhost:9092

# Set retention policy
kafka-configs.sh --alter --entity-type topics --entity-name my-topic --add-config retention.ms=604800000

# Enable log compaction
kafka-configs.sh --alter --entity-type topics --entity-name my-topic --add-config cleanup.policy=compact
```

## Broker Configs
```properties
# Replication & durability
min.insync.replicas=2
```

## Transactions (Producer)
```properties
transactional.id=my-tx-id
```

## Security CLI & Configs
```properties
# SASL authentication
security.protocol=SASL_SSL
sasl.mechanism=PLAIN

# SSL encryption
ssl.keystore.location=/path/to/keystore.jks

# ACLs example
kafka-acls.sh --add --allow-principal User:alice --operation Read --topic my-topic --bootstrap-server localhost:9092
```

## Monitoring & Alerting
- Monitor metrics: `kafka.server`, `kafka.producer`, `kafka.consumer`
- Enable logging: `log4j.properties`
- Set alerts for consumer lag, disk usage, broker failures
