package io.cdsoft.anypoint.mq.client.api;

public interface AnypointConfig {
    /**
     * Anypoint MQ client ID
     *
     * @return Client ID
     */
    String getClientId();

    /**
     * Anypoint MQ client secret
     *
     * @return Client secret
     */
    String getClientSecret();

    /**
     * @return
     */
    default String getGrantType() {
        return "client_credentials";
    }

    /**
     * The Anypoint MQ server URI
     *
     * @return MQ Server URI
     */
    String getServerUri();

    /**
     * Name of the Anypoint message queue where messages will be published
     *
     * @return Publish queue name
     */
    String getPublishDestination();

    /**
     * Name of the Anypoint message queue where messages will be retrieved
     *
     * @return Subscribe queue name
     */
    String getSubscribeDestination();

    /**
     * Get the logging level, One of
     * NONE, BASIC, HEADERS, BODY
     *
     * @return The logging level to use for HTTP requests
     */
    default String getLoggingLevel() {
        return "NONE";
    }

    /**
     * Get the Number of seconds to wait for HTTP connections
     *
     * @return Number of seconds
     */
    default Long getConnectTimeoutSeconds() {
        return 60L;
    }

    /**
     * Get the Number of seconds to wait for HTTP reads
     *
     * NOTE: This value should be longer than the poolingTime parameter on getMessage()
     *
     * @return Number of seconds
     */
    default Long getReadTimeoutSeconds() {
        return 60L;
    }

    /**
     * Get the Number of seconds to wait for HTTP writes
     *
     * @return Number of seconds
     */
    default Long getWriteTimeoutSeconds() {
        return 60L;
    }
}
