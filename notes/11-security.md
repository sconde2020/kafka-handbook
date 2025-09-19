# 11. Security

Security is essential when working with Apache Kafka to protect data and control access. The three core security concepts in Kafka are **Authentication**, **Authorization**, and **Encryption in transit**. Below, we explain each concept and show how to configure them using Spring Boot.

## Authentication

Authentication verifies the identity of users or applications connecting to Kafka. The most common method is SASL (Simple Authentication and Security Layer), which supports username/password, Kerberos, and OAuth-based authentication. SASL is flexible and integrates easily with external authentication systems.

**Spring Boot Example:**

```yaml
spring:
        kafka:
                properties:
                        security.protocol: SASL_SSL 
                        sasl.mechanism: PLAIN 
                        sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username="user" password="password";
```

## Authorization

Authorization determines what authenticated users are allowed to do. Kafka uses Access Control Lists (ACLs) to specify which users can perform actions such as reading from or writing to topics.

To use authorization, you must configure ACLs on your Kafka cluster. For example, you can allow a user to read from a topic by adding an ACL entry for that user.

**CLI Example:**

```bash
    kafka-acls.sh --bootstrap-server localhost:9092 \
        --add --allow-principal User:alice --operation Read --topic my-topic
```

This command allows the user `alice` to read from the topic `my-topic`.


## Encryption in Transit

Encryption in transit means that data moving between clients and Kafka brokers is kept safe from being seen or changed by others. Kafka uses SSL/TLS to encrypt this network traffic.

**Spring Boot Example:**

```yaml
spring:
    kafka:
        properties:
            security.protocol: SSL
            ssl.keystore.location: /path/to/keystore.jks
            ssl.keystore.password: yourpassword
            ssl.truststore.location: /path/to/truststore.jks
            ssl.truststore.password: yourpassword
```

In this configuration, `ssl.keystore.location` specifies the path to the keystore file containing the client's private key and certificate, while `ssl.truststore.location` points to the truststore file containing trusted CA certificates. Both are required for secure SSL/TLS communication between your Spring Boot application and Kafka brokers.

## Common Kafka Security Configurations

When securing Kafka, you typically combine different protocols and mechanisms based on your environment and requirements. The most important configuration properties are `security.protocol` and `sasl.mechanism`, which determine how authentication and encryption are handled between clients and brokers.

### Typical Configuration Values

- **security.protocol**:
    - `PLAINTEXT`: No authentication or encryption. Use only for local development or trusted internal networks.
    - `SSL`: Enables SSL/TLS encryption and certificate-based authentication.
    - `SASL_PLAINTEXT`: Uses SASL for authentication, but does not encrypt traffic.
    - `SASL_SSL`: Uses SASL for authentication and SSL/TLS for encryption. Recommended for production.

- **sasl.mechanism**:
    - `PLAIN`: Simple username/password authentication. Suitable for basic setups.
    - `SCRAM-SHA-256` / `SCRAM-SHA-512`: Secure password-based authentication. Recommended for production environments.
    - `GSSAPI`: Kerberos-based authentication. Common in enterprise setups.
    - `OAUTHBEARER`: OAuth 2.0 token-based authentication. Useful for cloud-native and microservices architectures.

### Example Use Cases

| Protocol         | Authentication      | Encryption      | Typical Use Case                        |
|------------------|--------------------|-----------------|-----------------------------------------|
| PLAINTEXT        | None               | None            | Local development in trusted environments |
| SSL              | Certificate-based  | Yes (SSL/TLS)   | Encryption and authentication via certificates |
| SASL_PLAINTEXT   | SASL (e.g., PLAIN) | None            | Authentication only, secure internal networks |
| SASL_SSL         | SASL (e.g., PLAIN) | Yes (SSL/TLS)   | Authentication and encryption in production |

Choose the configuration that matches your security needs and deployment environment.

## Example with Java

### [Kafka Security Configuration Example](../examples/kafka-security-config-example)

## Summary

- **Authentication**: Verifies identity using SASL or SSL.
- **Authorization**: Controls access using ACLs.
- **Encryption in transit**: Protects data using SSL/TLS.

Enable these features in your Spring Boot applications to ensure your Kafka system is secure.
