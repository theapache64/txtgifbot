package com.theapache64.tgb.data.local

import com.theapache64.tgb.data.local.base.AddQueryBuilder
import com.theapache64.tgb.data.local.base.BaseTable
import com.theapache64.tgb.data.local.base.SelectQueryBuilder
import com.theapache64.tgb.data.local.base.UpdateQueryBuilder
import com.theapache64.tgb.models.Hit
import org.omg.PortableInterceptor.SUCCESSFUL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HitsRepo @Inject constructor() : BaseTable<Hit>("hits") {

    companion object {
        private const val COLUMN_USER = "user"
        private const val COLUMN_MESSAGE_ID = "message_id"
        private const val COLUMN_FILE_ID = "file_id"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_IS_SUCCESS = "is_success"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"
        private const val COLUMN_WIDTH = "width"
        private const val COLUMN_HEIGHT = "height"
    }

    override fun add(newInstance: Hit): Boolean {
        return AddQueryBuilder.Builder(tableName)
                .add(COLUMN_USER, newInstance.user)
                .add(COLUMN_MESSAGE_ID, newInstance.messageId)
                .add(COLUMN_FILE_ID, newInstance.fileId)
                .add(COLUMN_WIDTH, newInstance.width)
                .add(COLUMN_HEIGHT, newInstance.height)
                .done()
    }

    fun getByMessageId(replyMsgId: Long): Hit? {
        return getItem(COLUMN_MESSAGE_ID, replyMsgId.toString())
    }

    override fun getItem(column: String, value: String): Hit? {
        return SelectQueryBuilder.Builder<Hit>(tableName) {

            Hit(
                    it.getLong(COLUMN_ID),
                    it.getString(COLUMN_USER),
                    it.getLong(COLUMN_MESSAGE_ID),
                    it.getString(COLUMN_FILE_ID),
                    it.getInt(COLUMN_WIDTH),
                    it.getInt(COLUMN_HEIGHT),
                    it.getString(COLUMN_TEXT),
                    it.getBoolean(COLUMN_IS_SUCCESS),
                    it.getString(COLUMN_CREATED_AT),
                    it.getString(COLUMN_UPDATED_AT)
            )

        }.select(arrayOf(
                COLUMN_ID,
                COLUMN_USER,
                COLUMN_MESSAGE_ID,
                COLUMN_FILE_ID,
                COLUMN_TEXT,
                COLUMN_WIDTH,
                COLUMN_HEIGHT,
                COLUMN_IS_SUCCESS,
                COLUMN_CREATED_AT,
                COLUMN_UPDATED_AT
        )).where(column, value).build().get()
    }

    override fun update(t: Hit): Boolean {
        return UpdateQueryBuilder.Builder(tableName)
                .set(COLUMN_TEXT, t.text)
                .set(COLUMN_IS_SUCCESS, t.isSuccess ?: false)
                .where(COLUMN_ID, t.id.toString())
                .build()
                .done()
    }
}