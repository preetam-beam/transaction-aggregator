package me.beam.pg.aggregator.helper;

import me.beam.persistence.specification.FilterOperation;
import me.beam.pg.aggregator.entity.TransactionReportEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QueryHelper {
    public static String prepareFilters(Map<String, String> searchMap,
                                        Map<String, FilterOperation> searchableFields) {
        StringBuilder filters = new StringBuilder();

        for (String key : searchMap.keySet()) {
            List<String> values = removeSpecialCharacters(key, searchMap.get(key));

            if (values.size() == 0) continue;

            if (searchableFields.containsKey(key)) {
                filters.append(" AND "); // Query already has a where statement

                if (searchableFields.get(key).equals(FilterOperation.IN)) {
                    filters.append(TransactionReportEntity.getFieldName(key))
                            .append(" IN (")
                            .append(String.join(",", values))
                            .append(")");
                } else {
                    filters.append(TransactionReportEntity.getFieldName(key))
                            .append(FilterOperation.getOperatorWithEnum(searchableFields.get(key)))
                            .append(values.get(0));
                }
            }

        }
        return filters.toString();
    }

    public static List<String> removeSpecialCharacters(String key, String value) {
        String[] values = value.replaceAll("[^-_.,a-zA-Z0-9]", "").split(",");
        List<String> out = new ArrayList<>();

        for (String s : values) {
            Optional<String> mayBeValue = TransactionReportEntity.convertField(key, s);
            mayBeValue.ifPresent(out::add);
        }

        return out;
    }
}
