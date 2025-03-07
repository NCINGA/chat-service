package com.ncinga.chatservice.config;


import com.ncinga.chatservice.dto.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class DataSinkConfiguration {
    @Bean
    public Sinks.Many<Message> chatSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
