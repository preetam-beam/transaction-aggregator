package me.beam.pg.aggregator.entity;

import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.beam.common.enums.TimeZone;
import me.beam.persistence.specification.FilterOperation;
import me.beam.zeus.common.enums.TransactionStatus;
import me.beam.zeus.common.enums.TransactionType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TransactionStatus transactionStatus;
    private String executionRequestExternalId;
    private String transactionSource;
    private String transactionReference;
    private String orderReference;
    private String operation;
    private String storeName;
    private String storeGroupName;
    private String storeExternalId;
    private String storeGroupExternalId;
    private String processingMode;
    private LocalDateTime transactionDate;
    private BigDecimal transactionAmount;
    private BigDecimal tipAmount;
    private String partner;
    private String program;
    private String gatewayId;
    private String terminalId;
    private String currency;
    private TransactionType transactionType;
    private TransactionStatus executionRequestStatus;
//    private String storeCategory; // TODO - where do we get it from
    private String accountHolderId;
    private String programName;
    private Long transactionDuration;
    private String authorizationCode;

    public static Map<String, FilterOperation> searchableFields() {
        Map<String, FilterOperation> fieldSet = new HashMap<>();
        fieldSet.put("requestTransactionId", FilterOperation.IN);
        return fieldSet;
    }

    public static String getFieldName(String key) {
        switch (key) {
            case "transactionDateFrom":
            case "transactionDateTo":
                return "transaction_date";
            default:
                return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
        }
    }

    public static Optional<String> convertField(String field, String value) {
        switch (field) {
            case "status": // We need enum ordinal
                try {
                    return Optional.of(String.valueOf(TransactionStatus.valueOf(value).ordinal()));
                } catch (IllegalArgumentException e) {
                    return Optional.empty();
                }
            case "transactionAmount": // BigDecimals can be added to native query directly
            case "id":
                return Optional.of(value);
            case "transactionDateFrom": // Parsing date
                return getDate(value, false);
            case "transactionDateTo":
                return getDate(value, true);
            default:
                return Optional.of("'" + value + "'"); // Adding quotes to string values to append in native query
        }
    }


    private static Optional<String> getDate(String dateStr, boolean addDay) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            // Add 1 day for upper date limit
            if (addDay) {
                c.add(Calendar.DATE, 1);
            }
            return Optional.of(" CAST('" + dateFormat.format(c.getTime()) +
                    "' AS timestamp) AT TIME ZONE '" + TimeZone.UAE.getSqlStr()
                    + "' AT TIME ZONE '" + TimeZone.UTC.getSqlStr() + "'");
        } catch (IllegalArgumentException | ParseException e) {
            log.error("Invalid Search parameter provided in ListRequestParam", e);
        }
        return Optional.empty();
    }
}
