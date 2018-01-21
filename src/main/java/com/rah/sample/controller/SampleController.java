package com.rah.sample.controller;

import com.rah.sample.models.DataObjectResponse;
import com.rah.sample.service.SampleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping(value = "v1/sample")
public class SampleController {

    @Inject SampleService sampleService;

    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET)
    public ResponseEntity healthCheck (
        @RequestHeader(value = "Authorization") String authorizationToken,
        @RequestHeader(value = "Tenant") String tenantUuid) {
        return new ResponseEntity<>("Hello : Sample application is running.", HttpStatus.OK);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public ResponseEntity<DataObjectResponse> getDataObject (
        @RequestHeader(value = "Authorization") String authorizationToken,
        @RequestHeader(value = "Tenant") String tenantUuid,
        @PathVariable String key) {
        return new ResponseEntity<>(sampleService.getDataObject(authorizationToken, tenantUuid, key), HttpStatus.OK);
    }
}
