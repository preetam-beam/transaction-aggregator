package me.beam.pg.aggregator.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.beam.zeus.common.enums.TransactionStatus;
import me.beam.zeus.common.enums.TransactionType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Slf4j
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataWarehouseTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long transactionId;
    private String executionRequestExternalId;
    private String transactionSource;
    private TransactionType transactionType;
    private String orderReference;
    private String gatewayId;
    private String transactionReference;
    private String storeExternalId;
    private String storeName;
    private String storeGroupExternalId;
    private String storeGroupName;
    private String accountHolderId;
    private String partnerExternalId;
    private String partnerName;
    private String mid;
    private String programName;
    private String transactionOperation;
    private TransactionStatus transactionStatus;
    private ZonedDateTime transactionCreationDate;
    private Long transactionDuration;
    private BigDecimal transactionAmount;
    private BigDecimal transactionTipAmount;
    private String currency;
    private String authorizationCode;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
