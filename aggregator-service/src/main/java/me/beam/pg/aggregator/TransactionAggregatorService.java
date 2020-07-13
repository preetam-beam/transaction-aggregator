package me.beam.pg.aggregator;

import me.beam.pg.dto.aggregator.consumer.AggregatorMessage;

public interface TransactionAggregatorService {
    void process(AggregatorMessage message);
}
