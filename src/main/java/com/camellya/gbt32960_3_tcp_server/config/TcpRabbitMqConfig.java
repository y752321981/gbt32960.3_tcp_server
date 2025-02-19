package com.camellya.gbt32960_3_tcp_server.config;

import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "rabbitmq")
@Configuration
public class TcpRabbitMqConfig {

    private String reportQueue;

    private String reportExchange;

    private String reportRoutingKey;


    @Bean
    public Queue queue() {
        return new Queue(reportQueue, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(reportExchange);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(reportRoutingKey);
    }


}
