package me.beam.pg.aggregator.repository;

import me.beam.pg.dto.reporting.datawarehouse.DataWhsTransactionDto;

import java.util.List;
import java.util.Set;

public interface TransactionAggregatorRepository {
    List<DataWhsTransactionDto> listTransactions(String transactionId);
}
