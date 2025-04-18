plugins {
  `java-platform`
}

data class DependencySet(val group: String, val version: String, val modules: List<String>)

val dependencyVersions = hashMapOf<String, String>()
rootProject.extra["versions"] = dependencyVersions

val DEPENDENCY_BOMS = listOf(
  "com.fasterxml.jackson:jackson-bom:2.18.1",
  "com.google.guava:guava-bom:33.3.1-jre",
  "com.google.protobuf:protobuf-bom:3.25.5",
  "com.linecorp.armeria:armeria-bom:1.30.1",
  "com.squareup.okhttp3:okhttp-bom:4.12.0",
  "com.squareup.okio:okio-bom:3.9.1", // applies to transitive dependencies of okhttp
  "io.grpc:grpc-bom:1.68.1",
  "io.netty:netty-bom:4.1.114.Final",
  "io.zipkin.brave:brave-bom:6.0.3",
  "io.zipkin.reporter2:zipkin-reporter-bom:3.4.2",
  "org.assertj:assertj-bom:3.26.3",
  "org.junit:junit-bom:5.11.3",
  "org.testcontainers:testcontainers-bom:1.20.3",
  "org.snakeyaml:snakeyaml-engine:2.8"
)

val autoValueVersion = "1.11.0"
val errorProneVersion = "2.35.1"
val jmhVersion = "1.37"
// Mockito 5.x.x requires Java 11 https://github.com/mockito/mockito/releases/tag/v5.0.0
val mockitoVersion = "4.11.0"
val slf4jVersion = "2.0.16"
val opencensusVersion = "0.31.1"
val prometheusClientVersion = "0.16.0"

val DEPENDENCIES = listOf(
  "com.google.auto.value:auto-value:${autoValueVersion}",
  "com.google.auto.value:auto-value-annotations:${autoValueVersion}",
  "com.google.errorprone:error_prone_annotations:${errorProneVersion}",
  "com.google.errorprone:error_prone_core:${errorProneVersion}",
  "com.google.errorprone:error_prone_test_helpers:${errorProneVersion}",
  "io.opencensus:opencensus-api:${opencensusVersion}",
  "io.opencensus:opencensus-impl-core:${opencensusVersion}",
  "io.opencensus:opencensus-impl:${opencensusVersion}",
  "io.opencensus:opencensus-exporter-metrics-util:${opencensusVersion}",
  "io.opencensus:opencensus-contrib-exemplar-util:${opencensusVersion}",
  "org.openjdk.jmh:jmh-core:${jmhVersion}",
  "org.openjdk.jmh:jmh-generator-bytecode:${jmhVersion}",
  "org.openjdk.jmh:jmh-generator-annprocess:${jmhVersion}",
  "org.mockito:mockito-core:${mockitoVersion}",
  "org.mockito:mockito-junit-jupiter:${mockitoVersion}",
  "org.slf4j:slf4j-simple:${slf4jVersion}",
  "org.slf4j:jul-to-slf4j:${slf4jVersion}",
  "io.prometheus:prometheus-metrics-shaded-protobuf:1.3.1",
  "io.prometheus:simpleclient:${prometheusClientVersion}",
  "io.prometheus:simpleclient_common:${prometheusClientVersion}",
  "io.prometheus:simpleclient_httpserver:${prometheusClientVersion}",
  "javax.annotation:javax.annotation-api:1.3.2",
  "com.github.stefanbirkner:system-rules:1.19.0",
  "com.google.api.grpc:proto-google-common-protos:2.48.0",
  "com.google.code.findbugs:jsr305:3.0.2",
  "com.google.guava:guava-beta-checker:1.0",
  "com.sun.net.httpserver:http:20070405",
  "com.tngtech.archunit:archunit-junit5:1.3.0",
  "com.uber.nullaway:nullaway:0.12.1",
  "edu.berkeley.cs.jqf:jqf-fuzz:1.7", // jqf-fuzz version 1.8+ requires Java 11+
  "eu.rekawek.toxiproxy:toxiproxy-java:2.1.7",
  "io.github.netmikey.logunit:logunit-jul:2.0.0",
  "io.jaegertracing:jaeger-client:1.8.1",
  "io.opentelemetry.contrib:opentelemetry-aws-xray-propagator:1.39.0-alpha",
  "io.opentelemetry.semconv:opentelemetry-semconv-incubating:1.28.0-alpha",
  "io.opentelemetry.proto:opentelemetry-proto:1.3.2-alpha",
  "io.opentracing:opentracing-api:0.33.0",
  "io.opentracing:opentracing-noop:0.33.0",
  "io.prometheus:prometheus-metrics-exporter-httpserver:1.3.3",
  "junit:junit:4.13.2",
  "nl.jqno.equalsverifier:equalsverifier:3.17.3",
  "org.awaitility:awaitility:4.2.2",
  "org.bouncycastle:bcpkix-jdk15on:1.70",
  "org.codehaus.mojo:animal-sniffer-annotations:1.24",
  "org.jctools:jctools-core:4.0.5",
  "org.junit-pioneer:junit-pioneer:1.9.1",
  "org.mock-server:mockserver-netty:5.15.0:shaded",
  "org.skyscreamer:jsonassert:1.5.3",
  "com.android.tools:desugar_jdk_libs:2.1.3",
)

javaPlatform {
  allowDependencies()
}

dependencies {
  for (bom in DEPENDENCY_BOMS) {
    api(enforcedPlatform(bom))
    val split = bom.split(':')
    dependencyVersions[split[0]] = split[2]
  }
  constraints {
    for (dependency in DEPENDENCIES) {
      api(dependency)
      val split = dependency.split(':')
      dependencyVersions[split[0]] = split[2]
    }
  }
}
