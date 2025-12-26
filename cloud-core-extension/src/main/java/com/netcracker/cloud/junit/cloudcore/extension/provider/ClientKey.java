package com.netcracker.cloud.junit.cloudcore.extension.provider;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class ClientKey {
    private final Integer requestTimeout;
    private final Integer websocketPingInterval;
    private final Integer watchReconnectInterval;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientKey clientKey = (ClientKey) o;
        return Objects.equals(requestTimeout, clientKey.requestTimeout)
                && Objects.equals(websocketPingInterval, clientKey.websocketPingInterval)
                && Objects.equals(watchReconnectInterval, clientKey.watchReconnectInterval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestTimeout, websocketPingInterval, watchReconnectInterval);
    }

    @Override
    public String toString() {
        return "ClientKey{" +
                "requestTimeout=" + requestTimeout +
                ", websocketPingInterval=" + websocketPingInterval +
                ", watchReconnectInterval=" + watchReconnectInterval +
                '}';
    }
}
