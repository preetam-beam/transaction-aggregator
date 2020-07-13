package me.beam.pg.aggregator.queries;

import me.beam.common.enums.TimeZone;

public class Queries {
    public static final String FIELD_PLACEHOLDER = "{FIELDS}";
    public static final String FILTER_PLACEHOLDER = "{FILTERS}";

    public static final String QUERY = "WITH request AS (SELECT er.id,\n" +
            "                        st.store_name,\n" +
            "                        st.store_external_id,\n" +
            "                        sg.store_group_name,\n" +
            "                        er.terminal_id,\n" +
            "                        er.status,\n" +
            "                        sg.store_external_id                 AS store_group_external_id,\n" +
            "                        er.store_model                       AS processing_mode,\n" +
            "                        er.created_date                      AS transaction_date,\n" +
            "                        er.total_amount                      AS transaction_amount,\n" +
            "                        CAST(er.ah_reward_amount AS NUMERIC) AS reward_amount,\n" +
            "                        CAST(er.ah_card_amount AS NUMERIC)   AS card_amount,\n" +
            "                        CAST(er.ah_tip_amount AS NUMERIC)    AS tip_amount,\n" +
            "                        er.tes_id                            AS request_tes_id,\n" +
            "                        er.settlement_reference              AS order_reference,\n" +
            "                        er.currency                          AS currency,\n" +
            "                        er.operation                         AS operation,\n" +
            "                        er.partner_id                        AS partner,\n" +
            "                        t.id                                 AS request_transaction_id,\n" +
            "                        (CAST(er.total_amount AS NUMERIC) -\n" +
            "                         CAST(er.ah_tip_amount AS NUMERIC))  AS sales_amount\n" +
            "\n" +
            "                 FROM execution_request er\n" +
            "                          LEFT JOIN stores st ON st.store_external_id = er.store_id\n" +
            "                          LEFT JOIN store_groups sg ON sg.id = st.group_id\n" +
            "                          LEFT JOIN transaction t on t.tes_external_id = er.tes_id),\n" +
            "\n" +
            "     all_transaction_status AS (\n" +
            "         SELECT tes_external_id AS status_external_id,\n" +
            "                status\n" +
            "         FROM (SELECT max(created_date) AS status_created_date, tes_external_id as tes_id\n" +
            "               FROM transaction\n" +
            "               GROUP BY tes_external_id) t\n" +
            "                  LEFT JOIN transaction\n" +
            "                            ON t.tes_id = transaction.tes_external_id AND\n" +
            "                               t.status_created_date = transaction.created_date\n" +
            "                  JOIN request ON tes_external_id = request.request_tes_id\n" +
            "         WHERE request.request_tes_id = transaction.tes_external_id\n" +
            "         GROUP BY tes_external_id, status\n" +
            "     ),\n" +
            "\n" +
            "     latest_status AS (\n" +
            "         SELECT all_transaction_status.status,\n" +
            "                transaction.transaction_reference AS transaction_reference,\n" +
            "                gateway_id,\n" +
            "                transaction.tes_external_id       AS status_external_id\n" +
            "         FROM transaction\n" +
            "                  JOIN all_transaction_status\n" +
            "                       ON transaction.tes_external_id = all_transaction_status.status_external_id\n" +
            "         WHERE transaction.tes_external_id = all_transaction_status.status_external_id\n" +
            "     )\n" +
            "\n" +
            "SELECT " + FIELD_PLACEHOLDER + " \n" +
            "FROM request,\n" +
            "     latest_status\n" +
            "WHERE latest_status.status_external_id = request_tes_id " + FILTER_PLACEHOLDER;

    public static final String COUNT_FIELDS = "1 AS id," +
            "       COUNT(request.id)                                AS total_records,\n" +
            "       SUM(CAST(request.transaction_amount AS NUMERIC)) AS total_amount,\n" +
            "       SUM(CAST(request.card_amount AS NUMERIC))        AS total_card_amount,\n" +
            "       SUM(CAST(request.reward_amount AS NUMERIC))      AS total_reward_amount,\n" +
            "       SUM(CAST(request.tip_amount AS NUMERIC))         AS total_tip_amount";

    public static final String DATA_FIELDS = "request.status    AS execution_request_status" +
            "request.request_tes_id    AS execution_request_external_id,\n" +
            "       CAST(request.transaction_date AS TIMESTAMP WITH TIME ZONE)\n" +
            "           AT TIME ZONE '" + TimeZone.UTC.getSqlStr() + "' " +
            "           AT TIME ZONE '"+ TimeZone.UTC.getSqlStr() +"' AS transaction_date,\n" +
            "       request.id,\n" +
            "       latest_status.transaction_id,\n" +
            "       latest_status.gateway_id,\n" +
            "       latest_status.status,\n" +
            "       request.currency,\n" +
            "       'Share'                                     AS program,\n" +
            "       'Offline'                                   AS transaction_source,\n" +
            "       (CASE\n" +
            "            WHEN latest_status.transaction_operation\n" +
            "                IN ('EARN', 'BURN', 'EARN_AND_BURN')\n" +
            "                THEN 'LOYALTY'\n" +
            "            ELSE 'CARD'\n" +
            "           END)                                    AS transaction_type,\n" +
            "       request.partner,\n" +
            "       latest_status.transaction_reference,\n" +
            "       latest_status.transaction_operation         AS operation,\n" +
            "       request.order_reference,\n" +
            "       request.store_name,\n" +
            "       request.store_group_name,\n" +
            "       request.store_external_id,\n" +
            "       request.store_group_external_id,\n" +
            "       request.processing_mode,\n" +
            "       request.terminal_id,\n" +
            "       request.transaction_amount,\n" +
            "       request.sales_amount,\n" +
            "       request.reward_amount,\n" +
            "       request.card_amount,\n" +
            "       request.tip_amount\n";

    public static final String SORT_BY = "ORDER BY request.transaction_date DESC";
}
