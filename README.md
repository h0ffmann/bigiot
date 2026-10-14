> **archived.** this repository is not maintained: 30 commits, from september 2020 to september 2021. nothing here was built or run again for this notice, so read it as a record and not as a tool.
>
> its own description: "data platform implementation for remote monitoring systems  using asynchronous messaging". it is mostly Scala and Shell. it has no licence file, so no licence is given for it.
>
> where the work is now:
>

| what | where |
|---|---|
| the whole story, with every work | [h0ffmann.github.io/h0ffmann](https://h0ffmann.github.io/h0ffmann/), doi [10.5281/zenodo.23352630](https://doi.org/10.5281/zenodo.23352630) |
| the sea, in Scala 3 | [marola.dev](https://marola.dev), doi [10.5281/zenodo.23224155](https://doi.org/10.5281/zenodo.23224155) |
| a wave model on GPUs | [ww3-gpu](https://h0ffmann.github.io/ww3-gpu/), doi [10.5281/zenodo.23221351](https://doi.org/10.5281/zenodo.23221351) |
| a benchmark for forecasts of the brazilian coast | [marola-bench](https://github.com/marola-dev/marola-research/tree/main/marola-bench), doi [10.5281/zenodo.23352633](https://doi.org/10.5281/zenodo.23352633) |
| the tools that build the rest | [picasso](https://github.com/h0ffmann/picasso), doi [10.5281/zenodo.23311208](https://doi.org/10.5281/zenodo.23311208) |
| a book on closed source | [closed source: the god that failed](https://h0ffmann.github.io/closed-source/), doi [10.5281/zenodo.23333656](https://doi.org/10.5281/zenodo.23333656) |

---

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