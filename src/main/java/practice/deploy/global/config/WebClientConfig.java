package practice.deploy.global.config;

import java.time.Duration;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    private final String upstageApiKey;
    private final String upstageApiUrl;

    public WebClientConfig(
            @Value("${upstage.api.key}") String upstageApiKey,
            @Value("${upstage.api.url}") String upstageApiUrl) {

        this.upstageApiKey = upstageApiKey;
        this.upstageApiUrl = upstageApiUrl;
    }

    @Bean
    public ReactorResourceFactory resourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        return factory;
    }

    @Bean
    public WebClient upstageWebClient() {
        Function<HttpClient, HttpClient> mapper =
                client ->
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(30))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .doOnConnected(
                                        connection ->
                                                connection
                                                        .addHandlerLast(new ReadTimeoutHandler(60))
                                                        .addHandlerLast(new WriteTimeoutHandler(10)));

        ClientHttpConnector connector = new ReactorClientHttpConnector(resourceFactory(), mapper);

        return WebClient.builder()
                .clientConnector(connector)
                .baseUrl(upstageApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + upstageApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}