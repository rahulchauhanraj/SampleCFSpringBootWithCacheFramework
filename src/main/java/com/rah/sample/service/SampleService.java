package com.rah.sample.service;

import com.rah.sample.builder.DataObjectResponseBuilder;
import com.rah.sample.cache.SampleCacheLoader;
import com.rah.sample.models.DataObject;
import com.rah.sample.models.DataObjectResponse;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SampleService {

    @Inject SampleCacheLoader sampleCacheLoader;

    public DataObjectResponse getDataObject (String authorizationToken, String tenantUuid, String sourceKey) {
        DataObject object = sampleCacheLoader.getObject(authorizationToken, tenantUuid, sourceKey);
        if (object == null) {
            return null;
        }
        return DataObjectResponseBuilder.createDataObjectResponse(object);
    }
}
