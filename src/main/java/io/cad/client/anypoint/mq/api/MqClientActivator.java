package io.cad.client.anypoint.mq.api;

import io.cad.client.anypoint.mq.impl.AnypointException;

import java.util.List;

public interface MqClientActivator<T> {

    void publishMessage(T payload) throws AnypointException;

    void publishMessages(List<T> payloads) throws AnypointException;

    T getMessage() throws AnypointException;

    List<T> getMessages(Integer batchSize, Integer poolingTime, Integer ttl) throws AnypointException;

}
