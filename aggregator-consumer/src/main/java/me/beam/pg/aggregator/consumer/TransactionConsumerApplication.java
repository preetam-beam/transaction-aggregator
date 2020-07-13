package me.beam.pg.aggregator.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class TransactionConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionConsumerApplication.class, args);
	}

}
