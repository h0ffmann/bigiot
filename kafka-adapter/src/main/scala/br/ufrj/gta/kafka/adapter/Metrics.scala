/*
 * Copyright 2020 Matheus Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufrj.gta.kafka.adapter

import io.prometheus.client.hotspot._
import io.prometheus.client.{ CollectorRegistry, Counter, Gauge }
import akka.http.scaladsl.server.Directives.{ complete, get, _ }
import akka.http.scaladsl.server.Route
import fr.davit.akka.http.metrics.core.scaladsl.server.HttpMetricsDirectives.{ metrics, _ }
import fr.davit.akka.http.metrics.prometheus.{ PrometheusRegistry, PrometheusSettings }
import fr.davit.akka.http.metrics.prometheus.marshalling.PrometheusMarshallers._
object Metrics {

  val prometheus: CollectorRegistry = CollectorRegistry.defaultRegistry
  val settings: PrometheusSettings  = PrometheusSettings.default
  val registry: PrometheusRegistry  = PrometheusRegistry(prometheus, settings)
  lazy val route: Route             = (get & path("metrics"))(metrics(registry))

  new StandardExports().register(prometheus)
  new MemoryPoolsExports().register(prometheus)
  new MemoryAllocationExports().register(prometheus)
  new BufferPoolsExports().register(prometheus)
  new GarbageCollectorExports().register(prometheus)
  new ThreadExports().register(prometheus)
  new ClassLoadingExports().register(prometheus)
  new VersionInfoExports().register(prometheus)

  val dummyCounter: Counter = Counter
    .build()
    .name("dummy_counter")
    .help("dummy counter.")
    .labelNames("device_id")
    .register(prometheus)

  val parserError: Counter = Counter
    .build()
    .name("parser_error")
    .help("Error while parsing device input.")
    .register(prometheus)

  val messageCount: Counter = Counter
    .build()
    .name("message_count")
    .help("Messages parsed into kafka.")
    .labelNames("device_id")
    .register(prometheus)

  val unavailableSensor: Counter = Counter
    .build()
    .name("unavailable_sensor")
    .help("Sensor with non success state.")
    .labelNames("device_id")
    .register(prometheus)

  val measurementSensor: Gauge = Gauge
    .build()
    .name("measurement_sensor")
    .help("Sensor current measurement.")
    .labelNames("device_id")
    .register(prometheus)

  val deviceCpuUsage: Gauge = Gauge
    .build()
    .name("device_cpu_usage")
    .help("Device CPU usage.")
    .labelNames("device_id")
    .register(prometheus)

  val deviceRamUsage: Gauge = Gauge
    .build()
    .name("device_ram_usage")
    .help("Device Ram usage.")
    .labelNames("device_id")
    .register(prometheus)

  val timeLag: Gauge = Gauge
    .build()
    .name("device_time_lag")
    .help("Device time lag.")
    .labelNames("device_id")
    .register(prometheus)
}
