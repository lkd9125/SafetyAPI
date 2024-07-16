package com.safety.law.global.external;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    private final String PUBLIC_DATA_PORTAL_URL = "https://apis.data.go.kr";

    @Bean
    public PublicDataPotalComponent publicDataPotalComponent(){
        RestClient restClient = RestClient.builder()
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", "*/*")
            .baseUrl(PUBLIC_DATA_PORTAL_URL)
            .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                throw new RestClientException("Client error: " + req.getURI());
            })
            .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
            
        return factory.createClient(PublicDataPotalComponent.class);
    }
    
}
