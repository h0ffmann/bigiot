# Big IoT

## MQTT Proxy
Proxy messages between a MQTT Broker and a Kafka Broker.

### Infrastructure
```bash
docker-compose up -d

...

Creating zk ... done
Creating kafka ... done
Creating emqx          ... done
Creating kafka-manager ... done
```

### Environment variables configuration
LOG_LEVEL=DEBUG   
MQTT_HOST=localhost   
MQTT_PORT=1883   
MQTT_USER=...   
MQTT_PASS=...   
KAFKA_HOST=localhost   
KAFKA_PORT=9092   
TOPICS=sensor_in_1,sensor_in_2,sensor_in_3   