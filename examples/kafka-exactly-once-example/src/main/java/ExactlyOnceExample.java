import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.KafkaException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;

public class ExactlyOnceExample {

    public static void main(String[] args) throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn");

        // ---------- Producer Config ----------
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Exactly-once producer config
        producerProps.put("acks", "all");
        producerProps.put("retries", Integer.toString(Integer.MAX_VALUE));
        producerProps.put("enable.idempotence", "true");
        producerProps.put("transactional.id", "exactly-once-producer-1");

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);
        producer.initTransactions();

        // Send user log events as JSON objects in a transaction
        try {
            producer.beginTransaction();
            for (int i = 1; i <= 5; i++) {
                String timestamp = LocalDateTime.now().format(formatter);
                String uuid = java.util.UUID.randomUUID().toString();

                String jsonMessage = String.format(
                        "{" +
                                "\"transactionId\":\"%s\"," +
                                "\"userId\":\"user-%d\"," +
                                "\"amount\":%.2f," +
                                "\"currency\":\"EUR\"," +
                                "\"status\":\"PENDING\"," +
                                "\"created_at\":\"%s\"" +
                                "}",
                        uuid,
                        i,
                        Math.random() * 1000, // simulate random amount
                        timestamp
                );

                producer.send(new ProducerRecord<>("transactions", null, jsonMessage));
                System.out.println("Sent: " + jsonMessage);
            }
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            producer.close();
            throw e;
        } catch (KafkaException e) {
            producer.abortTransaction();
        }
        producer.close();

        // ---------- Consumer Config ----------
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:9092");
        consumerProps.put("group.id", "transactions-consumer-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Exactly-once consumer config
        consumerProps.put("enable.auto.commit", "false");
        consumerProps.put("isolation.level", "read_committed");
        consumerProps.put("auto.offset.reset", "latest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("transactions"));

        System.out.println("Consumer started...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Consumed: " + record.value());
                Thread.sleep(2000); // simulate slow processing
            }
            if (!records.isEmpty()) {
                consumer.commitSync();
            }
        }
    }
}