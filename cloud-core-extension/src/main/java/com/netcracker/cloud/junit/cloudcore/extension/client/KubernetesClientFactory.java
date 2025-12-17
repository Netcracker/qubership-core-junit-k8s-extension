package com.netcracker.cloud.junit.cloudcore.extension.client;

import io.fabric8.kubernetes.client.KubernetesClient;

public interface KubernetesClientFactory {
    KubernetesClient getKubernetesClient(String context, String namespace);

    /**
     * Create or return cached Kubernetes client for given context/namespace with optional parameters.
     * Default implementation delegates to {@link #getKubernetesClient(String, String)} to preserve
     * backward compatibility for existing implementations.
     *
     * @param context                        kubernetes context name
     * @param namespace                      kubernetes namespace
     * @param requestTimeoutMillis           optional request timeout in milliseconds (may be null)
     * @param websocketPingIntervalMillis    optional websocket ping interval in milliseconds (may be null)
     * @param watchReconnectIntervalMillis   optional watch reconnect interval in milliseconds (may be null)
     * @return KubernetesClient instance
     */
    default KubernetesClient getKubernetesClient(String context, String namespace,
                                                 Integer requestTimeoutMillis,
                                                 Integer websocketPingIntervalMillis,
                                                 Integer watchReconnectIntervalMillis) {
        return getKubernetesClient(context, namespace);
    }

    String getCurrentContext();

    String getNamespace();
}
