# Big IoT

![Data Flow](./img/BigIoT.png)


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
http://localhost:9000/addCluster

### Environment variables configuration
```
LOG_LEVEL=DEBUG
CLIENT_ID=mqtt-proxy
MQTT_HOST=localhost
MQTT_PORT=1883
KAFKA_HOST=localhost
KAFKA_PORT=9092
TOPICS_IN=sensor-in-1
TOPICS_OUT=sensor-out-1
```