package me.beam.pg.aggregator.impl;

import me.beam.common.enums.Currency;
import me.beam.common.enums.TimeZone;
import me.beam.pg.aggregator.entity.TransactionReportEntity;
import me.beam.pg.aggregator.helper.QueryHelper;
import me.beam.pg.aggregator.queries.Queries;
import me.beam.pg.aggregator.repository.TransactionAggregatorRepository;
import me.beam.pg.dto.reporting.datawarehouse.DataWhsTransactionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Repository
public class TransactionAggregatorRepositoryImpl implements TransactionAggregatorRepository {

    private final String defaultCurrency = "AED";
    @PersistenceContext
    private EntityManager entityManager;
    @Value("${partners.maf}")
    private String mafPartnerId;

    @Override
    public List<DataWhsTransactionDto> listTransactions(String transactionId) {
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("requestTransactionId", transactionId);
        searchMap.put("executionRequestStatus", transactionId);

        String filters = QueryHelper.prepareFilters(
                searchMap,
                TransactionReportEntity.searchableFields()
        );
        List<TransactionReportEntity> transactionRecords = getResults(filters);

        return transactionRecords.stream().map(this::toDataWhsTransactionDto).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<TransactionReportEntity> getResults(String filters) {
        String dataQuery = Queries.QUERY
                .replace(Queries.FIELD_PLACEHOLDER, Queries.DATA_FIELDS)
                .replace(Queries.FILTER_PLACEHOLDER, filters);

        dataQuery += Queries.SORT_BY;

        Query query = entityManager.createNativeQuery(dataQuery, TransactionReportEntity.class);

        return query.getResultList();
    }

    private DataWhsTransactionDto toDataWhsTransactionDto(TransactionReportEntity entity) {
        return DataWhsTransactionDto.builder()
                .gatewayId(entity.getGatewayId())
                .transactionOperation(entity.getOperation())
                .transactionReference(entity.getTransactionReference())
                .orderReference(entity.getOrderReference())
                .partnerExternalId(getPartnerFromExternalId(entity.getPartner()))
                .partnerName(entity.getPartner())
                .programName(entity.getProgram())
                .transactionStatus(entity.getTransactionStatus())
                .storeGroupName(entity.getStoreGroupName())
                .storeGroupExternalId(entity.getStoreGroupExternalId())
                .storeName(entity.getStoreName())
                .storeExternalId(entity.getStoreExternalId())
                .storeName(entity.getStoreName())
                .transactionAmount(getAmount(entity.getTransactionAmount(), entity.getCurrency()))
                .transactionTipAmount(getAmount(entity.getTipAmount(), entity.getCurrency()))
                .currency(entity.getCurrency())
                .transactionCreationDate(entity.getTransactionDate().atZone(TimeZone.UAE.getZoneId()))
                .executionRequestStatus(entity.getExecutionRequestStatus())
                .accountHolderId(entity.getAccountHolderId())
                .authorizationCode(entity.getAuthorizationCode())
//                .storeCategory(entity.getStoreCategory())  // TODO
                .transactionDuration(entity.getTransactionDuration())
                .executionRequestExternalId(entity.getExecutionRequestExternalId())
                .transactionSource(entity.getTransactionSource())
                .transactionType(entity.getTransactionType())
                .build();
    }

    private String getPartnerFromExternalId(String partnerExternalId) {
        return partnerExternalId.equals(mafPartnerId) ? "MAF" : "";
    }

    private BigDecimal getAmount(BigDecimal value, String currency) {
        if (isNull(value)) return BigDecimal.ZERO;

        long denominations = Currency.getDenominationsFromName(currency.toUpperCase()).longValue();
        return value.divide(BigDecimal.valueOf(denominations));
    }
}
