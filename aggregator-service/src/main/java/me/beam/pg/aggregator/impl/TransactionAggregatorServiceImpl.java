package me.beam.pg.aggregator.impl;

import lombok.extern.slf4j.Slf4j;
import me.beam.pg.aggregator.TransactionAggregatorService;
import me.beam.pg.aggregator.repository.TransactionAggregatorRepository;
import me.beam.pg.dto.aggregator.consumer.AggregatorMessage;
import me.beam.pg.dto.reporting.datawarehouse.DataWhsTransactionDto;
import me.beam.pg.reporting.datawarehouse.repository.DataWhsTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionAggregatorServiceImpl implements TransactionAggregatorService {
    private final TransactionAggregatorRepository transactionAggregatorRepository;
    private final DataWhsTransactionRepository dataWhsTransactionRepository;

    public TransactionAggregatorServiceImpl(TransactionAggregatorRepository transactionAggregatorRepository,
                                            DataWhsTransactionRepository dataWhsTransactionRepository) {
        this.transactionAggregatorRepository = transactionAggregatorRepository;
        this.dataWhsTransactionRepository = dataWhsTransactionRepository;
    }

    @Override
    public void process(AggregatorMessage message) {
        String transactionId = null;

        int counter = 0;
        for (String field : message.getColumnnames()) {
            if (field.equalsIgnoreCase("id")) {
                transactionId = message.getColumnvalues().get(counter);
            }
            counter++;
        }

        if (transactionId != null)
            this.process(transactionId);
        else {
            //Log error message here.
            log.error("Invalid message received from AggregatorQueue: " + message.toString());
        }
    }

    private void process(String transactionId) {
        List<DataWhsTransactionDto> transactions = transactionAggregatorRepository.listTransactions(transactionId);
        dataWhsTransactionRepository.upsertTransactions(transactions);
    }
}
