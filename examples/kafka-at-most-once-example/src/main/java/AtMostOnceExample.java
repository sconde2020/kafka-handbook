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

public class AtMostOnceExample {

    public static void main(String[] args) throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn");

        // ---------- Producer Config ----------
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // At-most-once producer config
        producerProps.put("acks", "0");                   // No broker acknowledgment
        producerProps.put("retries", "0");                // No retries
        producerProps.put("enable.idempotence", "false"); // Disable idempotence

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        // Send messages with current timestamp
        for (int i = 0; i < 5; i++) {
            String timestamp = LocalDateTime.now().format(formatter);
            producer.send(new ProducerRecord<>("sensor-data", "temperature:" + timestamp));
            System.out.println("Sent: temperature:" + timestamp);
        }

        producer.close();

        // ---------- Consumer Config ----------
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:9092");
        consumerProps.put("group.id", "sensor-consumer-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Manual commit for at-most-once
        consumerProps.put("enable.auto.commit", "false"); // disable auto-commit

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("sensor-data"));

        System.out.println("Consumer started...");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); // Wait up to 100 ms for messages

            // ---------- Commit offsets BEFORE processing to simulate AMO ----------
            if (!records.isEmpty()) {
                consumer.commitSync(); // commit current offsets immediately
            }

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Consumed: " + record.value());
                Thread.sleep(2000); // simulate slow processing and stop it to check at-most-once
            }
        }
    }
}
