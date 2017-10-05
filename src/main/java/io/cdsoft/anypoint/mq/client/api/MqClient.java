package io.cdsoft.anypoint.mq.client.api;

import io.cdsoft.anypoint.mq.client.impl.AuthResponse;
import io.cdsoft.anypoint.mq.client.impl.MqMessage;
import io.cdsoft.anypoint.mq.client.impl.MqMessageReference;
import io.cdsoft.anypoint.mq.client.impl.MqStatusResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface MqClient {

    String BATCH_SIZE = "batchSize";
    Integer DEFAULT_BATCH_SIZE = 1;

    String POOLING_TIME = "poolingTime";
    Integer DEFAULT_POOLING_TIME = 10000;

    String LOCK_TTL = "lockTtl";
    Integer DEFAULT_LOCK_TTL = 120000;

    @FormUrlEncoded
    @POST("/api/v1/authorize")
    Call<AuthResponse> getAuthorization(@Field("grant_type") String grantType, @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret);

    /**
     * @param token     API Authorization token - does not prepend "bearer "
     * @param orgId     Anypoint Organization ID
     * @param envId     Anypoint Environment ID
     * @param dstId     Anypoint queue/exchange name
     * @param msgId     Unique message ID
     * @param mqMessage Message to publish
     * @return Status response message
     */
    @PUT("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages/{msgId}")
    Call<MqStatusResponse> putMessage(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId,
            @Path("msgId") String msgId,
            @Body MqMessage mqMessage);

    /**
     * @param token      API Authorization token - does not prepend "bearer "
     * @param orgId      Anypoint Organization ID
     * @param envId      Anypoint Environment ID
     * @param dstId      Anypoint queue/exchange name
     * @param mqMessages List of messages to publish. Set messageId in header property
     * @return List of status response message
     */
    @PUT("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages")
    Call<List<MqStatusResponse>> putAllMessages(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId,
            @Body List<MqMessage> mqMessages);

    /**
     * @param token API Authorization token - does not prepend "bearer "
     * @param orgId Anypoint Organization ID
     * @param envId Anypoint Environment ID
     * @param dstId Anypoint queue/exchange name
     * @return List of messages retrieved
     */
    @GET("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages")
    Call<List<MqMessage>> getMessages(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId);

    /**
     * @param token       API Authorization token - does not prepend "bearer "
     * @param orgId       Anypoint Organization ID
     * @param envId       Anypoint Environment ID
     * @param dstId       Anypoint queue/exchange name
     * @param batchSize   Batch Size 1-10; default=1; int
     * @param poolingTime Pooling Time &lt;= 20000; default=10000; int
     * @param lockTtl     Lock TTL &lt;= 86400000; default=120000; int
     * @return List of messages retrieved
     */
    @GET("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages")
    Call<List<MqMessage>> getMessages(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId,
            @Query(BATCH_SIZE) Integer batchSize,
            @Query(POOLING_TIME) Integer poolingTime,
            @Query(LOCK_TTL) Integer lockTtl);

    /**
     * @param token               API Authorization token - does not prepend "bearer "
     * @param orgId               Anypoint Organization ID
     * @param envId               Anypoint Environment ID
     * @param dstId               Anypoint queue/exchange name
     * @param mqMessageReferences List of messages to delete
     * @return Status response message
     */
    @DELETE("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages")
    Call<List<MqStatusResponse>> deleteMessages(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId,
            @Body List<MqMessageReference> mqMessageReferences);

    /**
     * @param token              API Authorization token - does not prepend "bearer "
     * @param orgId              Anypoint Organization ID
     * @param envId              Anypoint Environment ID
     * @param dstId              Anypoint queue/exchange name
     * @param msgId              Unique message ID
     * @param mqMessageReference Message to delete
     * @return Status response message
     */
    @DELETE("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages/{msgId}")
    Call<MqStatusResponse> deleteMessage(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId,
            @Path("msgId") String msgId,
            @Body MqMessageReference mqMessageReference);

    /**
     * @param token              API Authorization token - does not prepend "bearer "
     * @param orgId              Anypoint Organization ID
     * @param envId              Anypoint Environment ID
     * @param dstId              Anypoint queue/exchange name
     * @param msgId              Unique message ID
     * @param lckId              Unique lock ID
     * @param mqMessageReference Message lock to delete
     * @return Status response message
     */
    @DELETE("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages/{msgId}/locks/{lckId}")
    Call<MqStatusResponse> deleteLock(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId,
            @Path("msgId") String msgId,
            @Path("lckId") String lckId,
            @Body MqMessageReference mqMessageReference);

    /**
     * @param token               API Authorization token - does not prepend "bearer "
     * @param orgId               Anypoint Organization ID
     * @param envId               Anypoint Environment ID
     * @param dstId               Anypoint queue/exchange name
     * @param mqMessageReferences List of messages to remove locks from
     * @return List of messages
     */
    @DELETE("api/v1/organizations/{orgId}/environments/{envId}/destinations/{dstId}/messages/locks")
    Call<List<MqMessage>> deleteLocks(
            @Header("Authorization") String token,
            @Path("orgId") String orgId,
            @Path("envId") String envId,
            @Path("dstId") String dstId,
            @Body List<MqMessageReference> mqMessageReferences);

}

