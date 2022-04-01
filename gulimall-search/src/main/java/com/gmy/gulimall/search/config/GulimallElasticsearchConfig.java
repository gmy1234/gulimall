package com.gmy.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 *
 */
@SpringBootConfiguration
public class GulimallElasticsearchConfig {

    @Bean
    public RestHighLevelClient esRestClient(){

        RestClientBuilder builder = null;

        builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));

        RestHighLevelClient client =  new RestHighLevelClient(builder);

        return client;
    }

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }



}
