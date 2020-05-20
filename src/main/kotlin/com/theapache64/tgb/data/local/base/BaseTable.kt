package com.theapache64.tgb.data.local.base

import com.sun.istack.internal.Nullable
import com.theapache64.tgb.data.local.Connection
import java.sql.SQLException
import java.util.*

open class BaseTable<T>(val tableName: String) {

    @Throws(QueryBuilderException::class, SQLException::class)
    open fun getItem(column: String, value: String): T? {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    fun addv3(newInstance: T): String {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    @Throws(SQLException::class, QueryBuilderException::class)
    open fun add(newInstance: T): Boolean {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    @Throws(SQLException::class)
    fun update(whereColumn: String?, whereColumnValue: String, updateColumn: String, newUpdateColumnValue: String) {
        var isEdited = false
        val query = String.format("UPDATE %s SET %s = ? WHERE %s = ?;", tableName, updateColumn, whereColumn)
        val query2 = String.format("UPDATE %s SET %s = '$newUpdateColumnValue' WHERE %s = '$whereColumnValue';", tableName, updateColumn, whereColumn)
        val con = Connection.connection
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, newUpdateColumnValue)
            ps.setString(2, whereColumnValue)
            isEdited = ps.executeUpdate() == 1
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        if (!isEdited) {
            throw SQLException("Failed to update $updateColumn")
        }
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    fun getItem(column1: String?, value1: String?, column2: String?, value2: String?): T {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    @Throws(SQLException::class, QueryBuilderException::class)
    fun update(t: T): Boolean {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    protected fun isExist(t: T): Boolean {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    protected fun isExist(whereColumn: String?, whereColumnValue: String?): Boolean {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    fun getItem(byColumn: String?, byValue: String?, columnToReturn: String?, isActive: Boolean): String? {
        val query = String.format("SELECT %s FROM %s WHERE %s = ? %s ORDER BY id DESC LIMIT 1", columnToReturn, tableName, byColumn, if (isActive) " AND is_active = 1 " else "")
        var resultValue: String? = null
        val con = Connection.connection
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, byValue)
            val rs = ps.executeQuery()
            if (rs.first()) {
                resultValue = rs.getString(columnToReturn)
            }
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return resultValue
    }

    fun isExist(whereColumn1: String?, whereColumnValue1: String?, whereColumn2: String?, whereColumnValue2: String?): Boolean {
        var isExist = false
        val query = String.format("SELECT id FROM %s WHERE %s = ? AND %s = ? LIMIT 1", tableName, whereColumn1, whereColumn2)
        val con = Connection.connection
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, whereColumnValue1)
            ps.setString(2, whereColumnValue2)
            val rs = ps.executeQuery()
            isExist = rs.first()
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return isExist
    }

    @Throws(QueryBuilderException::class, SQLException::class)
    fun getAll(whereColumn: String?, whereColumnValue: String?): List<T> {
        throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
    }

    @get:Throws(QueryBuilderException::class, SQLException::class)
    val all: List<T>
        get() {
            throw IllegalArgumentException(ERROR_MESSAGE_UNDEFINED_METHOD)
        }

    fun getTotal(victimId: String?): Int {
        var totalCount = 0
        val query = String.format("SELECT COUNT(id) AS total_rows FROM %s  WHERE victim_id = ?", tableName)
        val con = Connection.connection
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, victimId)
            val rs = ps.executeQuery()
            if (rs.first()) {
                totalCount = rs.getInt(COLUMN_AS_TOTAL_ROWS)
            }
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return totalCount
    }

    @JvmOverloads
    fun delete(whereColumn: String?, whereColumnValue: String?, @Nullable whereColumn2: String? = null, @Nullable whereColumnValue2: String? = null): Boolean {
        var isDeleted = false
        var query: String? = null
        query = if (whereColumn2 == null) {
            String.format("DELETE FROM %s WHERE %s = ?", tableName, whereColumn)
        } else {
            String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", tableName, whereColumn, whereColumn2)
        }
        val con = Connection.connection
        try {
            val ps = con.prepareStatement(query)
            if (whereColumn2 == null) {
                ps.setString(1, whereColumnValue)
            } else {
                ps.setString(1, whereColumnValue)
                ps.setString(2, whereColumnValue2)
            }
            isDeleted = ps.executeUpdate() > 0
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return isDeleted
    }

    fun getLast(count: Int): List<T>? {
        return null
    }

    fun getLike(column1: String?, value1: String?, column2: String?, value2Like: String?, columnToReturn: String?): List<String?>? {
        val query = String.format("SELECT %s FROM %s WHERE %s = ? AND %s LIKE '%%%s%%' AND is_active = 1", columnToReturn, tableName, column1, column2, value2Like)
        var resultValues: MutableList<String?>? = null
        val con = Connection.connection
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, value1)
            val rs = ps.executeQuery()
            if (rs.first()) {
                resultValues = ArrayList()
                do {
                    resultValues.add(rs.getString(columnToReturn))
                } while (rs.next())
            }
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return resultValues
    }

    fun getItem(column1: String?, value1: String?, column2: String?, value2: String?, columnToReturn: String?): String? {
        val query = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ? AND is_active = 1 LIMIT 1", columnToReturn, tableName, column1, column2)
        var resultValue: String? = null
        val con = Connection.connection
        try {
            val ps = con.prepareStatement(query)
            ps.setString(1, value1)
            ps.setString(2, value2)
            val rs = ps.executeQuery()
            if (rs.first()) {
                resultValue = rs.getString(columnToReturn)
            }
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                con.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return resultValue
    }

    companion object {

        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_IS_ACTIVE = "is_active"
        const val TRUE = "1"
        private const val ERROR_MESSAGE_UNDEFINED_METHOD = "Undefined method."
        private const val COLUMN_AS_TOTAL_ROWS = "total_rows"
        const val FALSE = "0"
        val allCounts: Map<String, Int>
            get() = HashMap()

        protected fun getGroupDecatenated(data: String?): Array<String>? {
            return data?.split(",")?.toTypedArray()
        }

        @Throws(SQLException::class)
        fun manageError(error: String?): Boolean {
            if (error != null) {
                throw SQLException(error)
            }
            return true
        }
    }

}