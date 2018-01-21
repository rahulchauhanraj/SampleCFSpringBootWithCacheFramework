package com.rah.sample.builder;

import com.rah.sample.models.DataObject;
import com.rah.sample.models.DataObjectResponse;

public class DataObjectResponseBuilder {
    public static DataObjectResponse createDataObjectResponse (DataObject object) {
        return new DataObjectResponse(object.getId(), object.getName(), object.getValue());
    }
}
