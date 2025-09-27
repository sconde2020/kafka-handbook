# Setups

## How to setup JConsole locally

1. **Install JConsole:**  
    JConsole comes with the Java Development Kit (JDK). If you have the JDK installed, JConsole is already available.  
    - On Linux or macOS, run:  
```bash
    export JMX_PORT=9999
    jconsole
```

    - On Windows, open Command Prompt and run:  
```powershell
    jconsole.exe
```

2. **Connect to Kafka:**  
    - Start JConsole.
    - Select the path of your local server

3. **View Metrics:**  
    - Browse the MBeans tree to find Kafka metrics.
    - Look for metrics like message rates, errors, and resource usage.

---

## How to setup Prometheus

1. **Install Prometheus:**  
    Download and install Prometheus from the [official website](https://prometheus.io/download/).

2. **Set up Kafka Exporter:**  
    - Download a Kafka exporter (e.g., [kafka_exporter](https://github.com/danielqsj/kafka_exporter)).
    - Run the exporter and point it to your Kafka cluster.

3. **Configure Prometheus:**  
    - Edit `prometheus.yml` to add the Kafka exporter as a target:
      ```yaml
      scrape_configs:
         - job_name: 'kafka'
            static_configs:
              - targets: ['localhost:9308']
      ```
    - Start Prometheus.

4. **Verify Metrics:**  
    - Open Prometheus web UI (`http://localhost:9090`).
    - Search for Kafka metrics (e.g., `kafka_server_broker_topic_metrics_messages_in_total`).

---

## How to setup Grafana

1. **Install Grafana:**  
    Download and install Grafana from the [official website](https://grafana.com/grafana/download).

2. **Connect to Prometheus:**  
    - Log in to Grafana.
    - Add Prometheus as a data source (`http://localhost:9090`).

3. **Import Kafka Dashboards:**  
    - Go to "Dashboards" and click "Import".
    - Use a pre-built Kafka dashboard from [Grafana Labs](https://grafana.com/grafana/dashboards/).

4. **View Kafka Metrics:**  
    - Open the dashboard to see graphs for Kafka performance, errors, and resource usage.

**Tip:** Customize dashboards to focus on the metrics most important for your Kafka setup.