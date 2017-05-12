package io.cad.client.anypoint.mq.api;

public interface AnypointConfig {
    String getClientId();

    String getClientSecret();

    String getGrantType();

    String getServerUri();

    String getPublishDestination();

    String getSubscribeDestination();

    String getLoggingLevel();
}
