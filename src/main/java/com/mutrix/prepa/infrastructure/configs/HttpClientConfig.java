package com.mutrix.prepa.infrastructure.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
//import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
//import reactor.netty.http.client.HttpClient;

import java.time.Duration;

import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;


@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {
    @Value("${payment.campay.url}")
    private String campayBaseUrl;

    @Value("${mail.resend.url}")
    private String resendBaseUrl;

    @Value("${mail.resend.apiKey}")
    private String resendApiKey;

//    private HttpClient httpClient(Integer timeOut){
//        return  HttpClient.create().responseTimeout(Duration.ofSeconds(timeOut==null?5:timeOut));
//    }

    @Bean
    public RestClient campayRestClient(){
        return RestClient.builder()
                .baseUrl(campayBaseUrl)
                .defaultHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .requestFactory(new ReactorClientHttpRequestFactory(httpClient(null)))
                .build();
    }

    @Bean
    public RestClient resendRestClient(){
        return RestClient.builder()
                .baseUrl(resendBaseUrl)
                .defaultHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .requestFactory(new ReactorClientHttpRequestFactory(httpClient(10)))
                .defaultHeader("api-key", resendApiKey)
                .build();
    }
}
