package com.switchscale.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    
    @Bean
    @ConditionalOnProperty(name = "order.events.kafka.enabled", havingValue = "true")
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name("order-placed")
                .partitions(1)
                .replicas(1)
                .build();
    }

    
}
