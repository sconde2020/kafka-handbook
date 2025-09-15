import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;

public class AtLeastOnceExample {

    public static void main(String[] args) throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn");

        // ---------- Producer Config ----------
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // At-least-once producer config
        producerProps.put("acks", "all");                   // Wait for all replicas to acknowledge
        producerProps.put("retries", "3");                  // Enable retries

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        // Send simple user log events
        for (int i = 1; i <= 5; i++) {
            String timestamp = LocalDateTime.now().format(formatter);
            String message = "user-log-" + i + " created at " + timestamp;
            producer.send(new ProducerRecord<>("user-log", message));
            System.out.println("Sent: " + message);
        }

        producer.close();

        // ---------- Consumer Config ----------
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:9092");
        consumerProps.put("group.id", "user-log-consumer-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Manual commit for at-least-once
        consumerProps.put("enable.auto.commit", "false"); // disable auto-commit
        consumerProps.put("auto.offset.reset", "earliest"); // read from start if no offset

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("user-log"));

        System.out.println("Consumer started...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); // Wait up to 100 ms for messages

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Consumed: " + record.value());
                Thread.sleep(2000); // simulate slow processing and restart to see potential duplications but no lost
            }

            // ---------- Commit offsets AFTER processing to ensure ALO ----------
            if (!records.isEmpty()) {
                consumer.commitSync(); // commit offsets after processing
            }
        }
    }
}
