package com.shyam.gn.gracenoteparserbatch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Configuration
@EnableBatchProcessing
public class LoadProviderConfiguration {

    @Autowired
    RestTemplate restTemplate;

    @Value("${gracenote.url}")
    private String gracenoteUrl;

    @Value("file:/provider-lookup-request-template.txt")
    private Resource requestTemplate;

    @Value("${gracenote.client}")
    private String gracenoteClient;

    @Value("${gracenote.user}")
    private String gracenoteUser;

    @Value("${application.postalcode.lookup}")
    private String postalCode;

    @Bean
    @StepScope
    public ItemReader<String> reader(){
        return () -> {

            String requestTemplate = new String(requestTemplate.getBytes());
            requestTemplate = requestTemplate.replace("##CLIENT##", gracenoteClient)
                    .replace("##USER##", gracenoteUser)
                    .replace("##POSTALCODE##", postalCode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> request = new HttpEntity<String>(requestTemplate, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(gracenoteUrl, request, String.class);

            return response;
        };
    }
}
