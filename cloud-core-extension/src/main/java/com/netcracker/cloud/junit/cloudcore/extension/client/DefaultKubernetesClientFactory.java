package com.netcracker.cloud.junit.cloudcore.extension.client;

import com.netcracker.cloud.junit.cloudcore.extension.annotations.Priority;
import io.fabric8.kubernetes.api.model.NamedContext;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import com.netcracker.cloud.junit.cloudcore.extension.provider.ClientKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.fabric8.kubernetes.client.utils.HttpClientUtils.getHttpClientFactory;

@Priority
public class DefaultKubernetesClientFactory implements AutoCloseable, KubernetesClientFactory {

    private final Config config;

    public DefaultKubernetesClientFactory() {
        this(new ConfigBuilder().build());
    }

    public DefaultKubernetesClientFactory(Config config) {
        this.config = config;
    }

    private final static ConcurrentHashMap<ClientKey, KubernetesClient> clientsMap = new ConcurrentHashMap<>();

    // ClientKey moved to provider package: com.netcracker.cloud.junit.cloudcore.extension.provider.ClientKey

    public Collection<String> getKubernetesContexts() {
        return config.getContexts().stream().map(NamedContext::getName).toList();
    }

    public KubernetesClient getKubernetesClient(String context, String namespace) {
        return getKubernetesClient(context, namespace, null, null, null);
    }

    public KubernetesClient getKubernetesClient(String context, String namespace, Integer requestTimeoutMillis,
                                                Integer websocketPingIntervalMillis, Integer watchReconnectIntervalMillis) {
        return clientsMap.computeIfAbsent(new ClientKey(context, namespace, requestTimeoutMillis,
                websocketPingIntervalMillis, watchReconnectIntervalMillis), clientKey -> {
            String cloud = clientKey.getCloud();
            NamedContext namedContext = config.getContexts().stream()
                    .filter(c -> Objects.equals(c.getName(), cloud)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown context: '%s'. Known contexts:\n[%s]",
                            cloud, String.join(",\n", getKubernetesContexts()))));
            Config config;
            if (Objects.equals(cloud, this.config.getCurrentContext().getName())) {
                config = this.config;
            } else {
                config = Config.autoConfigure(namedContext.getName());
            }
            int resolvedTimeout = resolveIntParameter(cloud, clientKey.getRequestTimeout(), "requestTimeout", 10000);
            int resolvedWebsocketPing = resolveIntParameter(cloud, clientKey.getWebsocketPingInterval(), "websocketPingInterval", 10000);
            int resolvedWatchReconnect = resolveIntParameter(cloud, clientKey.getWatchReconnectInterval(), "watchReconnectInterval", 5000);

            config = new ConfigBuilder(config)
                    .withNamespace(clientKey.getNamespace())
                    .withTrustCerts(true)
                    .withDisableHostnameVerification(true)
                    .withRequestRetryBackoffLimit(3)
                    .withWatchReconnectLimit(3)
                    .withRequestTimeout(resolvedTimeout)
                    .withWebsocketPingInterval(resolvedWebsocketPing)
                    .withWatchReconnectInterval(resolvedWatchReconnect)
                    .build();
            return new KubernetesClientBuilder().withConfig(config)
                    .withHttpClientFactory(getHttpClientFactory())
                    .withHttpClientBuilderConsumer(builder -> builder.connectTimeout(15, TimeUnit.SECONDS))
                    .build();
        });
    }
    private int resolveIntParameter(String context, Integer passedValue, String propNameSuffix, int defaultValue) {
        if (passedValue != null && passedValue > 0) {
            return passedValue;
        }
        try {
            String propName = String.format("clouds.%s.%s", context, propNameSuffix);
            String prop = System.getProperty(propName);
            if (prop != null && !prop.isBlank()) {
                return Integer.parseInt(prop);
            }
        } catch (NumberFormatException ignored) {
            // fall through to default
        }
        return defaultValue;
    }

    @Override
    public String getCurrentContext() {
        return config.getCurrentContext().getName();
    }

    @Override
    public String getNamespace() {
        return config.getNamespace();
    }

    @Override
    public void close() {
        HashMap<ClientKey, KubernetesClient> copy = new HashMap<>(clientsMap);
        clientsMap.clear();
        copy.values().forEach(KubernetesClient::close);
    }
}
