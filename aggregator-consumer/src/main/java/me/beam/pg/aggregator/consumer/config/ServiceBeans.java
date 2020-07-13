package me.beam.pg.aggregator.consumer.config;

import me.beam.message.common.consume.MessageConsumer;
import me.beam.message.consumer.activemq.ActiveMqDelayedMessageProducer;
import me.beam.message.consumer.activemq.ActiveMqMessageConsumer;
import me.beam.pg.aggregator.consumer.handler.TransactionMessageHandler;
import me.beam.soul.util.parser.JacksonParser;
import org.apache.activemq.command.ActiveMQMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class ServiceBeans {

    @Bean
    public JacksonParser jacksonParser() {
        return new JacksonParser();
    }

    @Bean
    public MessageConsumer<ActiveMQMessage> messageConsumer(TransactionMessageHandler consumer) {
        return new ActiveMqMessageConsumer(consumer);
    }

    @Bean
    public ActiveMqDelayedMessageProducer activeMqDelayedMessageProducer(JmsTemplate jmsTemplate) {
        return new ActiveMqDelayedMessageProducer(jmsTemplate);
    }

}
