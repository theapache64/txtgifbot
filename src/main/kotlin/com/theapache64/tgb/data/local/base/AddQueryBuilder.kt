package com.theapache64.tgb.data.local.base

import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*


class AddQueryBuilder {
    class Builder(private val tableName: String) {
        private val map: MutableMap<String, Any>

        fun add(column: String, value: Any): Builder {
            map[column] = value
            return this
        }

        @Throws(SQLException::class, QueryBuilderException::class)
        fun done(): Boolean {
            return doneAndReturn() != -1L
        }

        @Throws(SQLException::class, QueryBuilderException::class)
        fun doneAndReturn(): Long {
            var rowId: Long = -1
            if (map.isEmpty()) {
                throw QueryBuilderException("No data in insert query")
            }

            //Building query
            var error: String? = null
            val builder = StringBuilder("INSERT INTO ").append(tableName).append("(")
            val columns: List<String> = ArrayList(map.keys)
            val totalColumns = columns.size
            for (column in columns) {
                builder.append(column).append(",")
            }
            builder.append(") VALUES (").append(String.format("%0" + totalColumns + "d", 0).replace("0", "?,")).append(")")
            val query = builder.toString().replace(",\\)".toRegex(), "\\)")
            val con = com.theapache64.tgb.data.local.Connection.connection
            try {
                val ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
                var i = 1
                for ((_, value) in map) {
                    when (value) {
                        is Boolean -> ps.setBoolean(i++, value)
                        is Int -> ps.setInt(i++, value)
                        is Long -> ps.setLong(i++, value)
                        is Double -> ps.setDouble(i++, value)
                        else -> ps.setString(i++, value.toString())
                    }
                }
                ps.executeUpdate()
                val rs = ps.generatedKeys
                if (rs.first()) {
                    rowId = rs.getLong(1)
                }
                rs.close()
                ps.close()
            } catch (e: SQLException) {
                e.printStackTrace()
                error = e.message
            } finally {
                try {
                    con.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
            manageError(error)
            return rowId
        }

        @Throws(SQLException::class)
        private fun manageError(error: String?) {
            if (error != null) {
                throw SQLException(error)
            }
        }

        init {
            map = HashMap()
        }
    }
}