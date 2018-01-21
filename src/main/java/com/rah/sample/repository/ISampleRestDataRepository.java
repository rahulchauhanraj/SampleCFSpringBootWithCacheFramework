package com.rah.sample.repository;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

public interface ISampleRestDataRepository {

    @RequestLine("GET /{sourceKey}")
    @Headers({"Authorization: {authorizationToken}", "Tenant: {tenantUuid}", "Content-Type: application/json", "Accept: */*"})
    Response getDataObject(@Param("authorizationToken") String authorizationToken, @Param("tenantUuid") String tenantUuid, @Param("sourceKey") String sourceKey);
}
