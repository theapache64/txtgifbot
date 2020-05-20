package com.theapache64.tgb.data.local.base;


import com.theapache64.tgb.data.local.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateQueryBuilder {

    private final String tableName;
    private final Map<String, String> columnValues;
    private final String whereColumn;
    private final String whereValue;

    public UpdateQueryBuilder(String tableName, Map<String, String> columnValues, String whereColumn, String whereValue) {
        this.tableName = tableName;
        this.columnValues = columnValues;
        this.whereColumn = whereColumn;
        this.whereValue = whereValue;
    }


    public static class Builder {

        private final String tableName;
        private final Map<String, String> columnValues = new HashMap<>();
        private String whereColumn;
        private String whereValue;

        public Builder(String tableName) {
            this.tableName = tableName;
        }

        public Builder set(final String column, final String value) {
            columnValues.put(column, value);
            return this;
        }

        public Builder set(final String column, final boolean value) {
            return set(column, value ? "1" : "0");
        }

        public Builder where(final String column, final String value) {
            this.whereColumn = column;
            this.whereValue = value;
            return this;
        }

        public UpdateQueryBuilder build() {
            return new UpdateQueryBuilder(tableName, columnValues, whereColumn, whereValue);
        }
    }

    public boolean done() throws QueryBuilderException, SQLException {

        String error = null;
        boolean isUpdated = false;
        StringBuilder queryBuilder = new StringBuilder("UPDATE ").append(tableName);
        if (columnValues.isEmpty()) {
            throw new QueryBuilderException("No columns set to update");
        }

        queryBuilder.append(" SET ");

        for (Map.Entry<String, String> entry : columnValues.entrySet()) {
            queryBuilder.append(entry.getKey()).append(" = ?,");
        }

        //Removing lst comma
        queryBuilder = new StringBuilder(queryBuilder.substring(0, queryBuilder.length() - 1));

        //Appending where section
        if (whereColumn != null) {
            queryBuilder.append(" WHERE ").append(whereColumn).append(" = ?;");
        }


        //Now, let's bind the values
        java.sql.Connection con = Connection.INSTANCE.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(queryBuilder.toString());


            int i = 1;
            for (Map.Entry<String, String> entry : columnValues.entrySet()) {
                ps.setString(i++, entry.getValue());
            }

            if (whereColumn != null) {
                ps.setString(columnValues.size() + 1, whereValue);
            }

            isUpdated = ps.executeUpdate() > 0;
            ps.close();


        } catch (SQLException e) {
            e.printStackTrace();
            error = e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        BaseTable.Companion.manageError(error);

        return isUpdated;
    }
}
