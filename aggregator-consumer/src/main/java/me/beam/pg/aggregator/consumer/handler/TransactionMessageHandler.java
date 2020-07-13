package me.beam.pg.aggregator.consumer.handler;

import lombok.extern.slf4j.Slf4j;
import me.beam.message.common.consume.MessageHandler;
import me.beam.pg.aggregator.TransactionAggregatorService;
import me.beam.pg.dto.aggregator.consumer.AggregatorMessage;
import me.beam.soul.util.parser.JacksonParser;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionMessageHandler implements MessageHandler<ActiveMQMessage> {

    private final JacksonParser jacksonParser;
    private final TransactionAggregatorService transactionAggregatorService;
//    private FailureHandler failureHandler;

    public TransactionMessageHandler(JacksonParser jacksonParser,
                                     TransactionAggregatorService transactionAggregatorService) {
        this.jacksonParser = jacksonParser;
        this.transactionAggregatorService = transactionAggregatorService;
//        this.failureHandler = failureHandler;
    }

    @Override
    public void onMessage(ActiveMQMessage message) {
        AggregatorMessage aggregatorMessage;
        try {
            if (message instanceof ActiveMQTextMessage) {
                String textMessage = ((ActiveMQTextMessage) message).getText();
                aggregatorMessage = jacksonParser.fromJson(textMessage, AggregatorMessage.class);
            } else {
//                Failure failure = buildFailure(message, String.format("Cannot handle message of type {%s}", message.getClass()));
//                failureHandler.handle(failure);
                return;
            }
        } catch (Exception e) {
            log.error("Error while parsing message cause {}", e.getMessage(), e);
//            buildFailure(message, String.format("Error while parsing message cause {%s}", e.getMessage()));
            return;
        }
        onMessage(aggregatorMessage);
    }

//    private Failure buildFailure(ActiveMQMessage message, String error) {
//        Failure failure = new Failure();
//        failure.setOriginalPayload(message.toString());
//        failure.setErrorMessage(error);
//        return failure;
//    }

    protected void onMessage(AggregatorMessage message) {
        transactionAggregatorService.process(message);
    }
}
