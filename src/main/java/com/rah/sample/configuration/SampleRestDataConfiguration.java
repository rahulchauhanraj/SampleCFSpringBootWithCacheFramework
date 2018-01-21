package com.rah.sample.configuration;

import com.rah.sample.repository.ISampleRestDataRepository;
import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class SampleRestDataConfiguration {

    @Value("${SAMPLE_DATA_SERVICE_BASE_URL:https://rsc-sample-spring-rest.run.aws-usw02-pr.ice.sample.io/v1/sample}")
    private String serviceUrl;

    @Inject
    Encoder encoder;

    @Inject
    Decoder decoder;

    @Bean
    public ISampleRestDataRepository sampleRestDataRepository(){

        return Feign.builder()
            .encoder(encoder)
            .decoder(decoder)
            .logLevel(Logger.Level.BASIC)
            .target(ISampleRestDataRepository.class, serviceUrl);
    }
}
