# Big IoT

- [Proposal](./docs/proposal/PropostaTCCMHoffmann-unsigned.pdf)
<p align="center">
  <img src="https://github.com/h0ffmann/bigiot/blob/master/img/bigiot.png" alt="Big IoT Architecture"/>
</p>

# Infrastructure
```bash
docker-compose up -d 
...
Creating network "bigiot_default" with the default driver
Creating zk         ... done
Creating prometheus ... done
Creating grafana    ... done
Creating kafka      ... done
Creating emqx          ... done
Creating kafka-manager ... done
```

## Kafka adapter
Proxy messages between EMQ X Broker and the Kafka Broker.

- http://localhost:9000/addCluster

### Environment variables configuration
```bash
CLIENT_ID=mqtt-proxy
MQTT_HOST=localhost
MQTT_PORT=1883
KAFKA_HOST=localhost
KAFKA_PORT=9092
TOPICS_IN=sensor-in-1
TOPICS_OUT=sensor-out-1
```