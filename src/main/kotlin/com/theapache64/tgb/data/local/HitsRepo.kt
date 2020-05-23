package com.theapache64.tgb.data.local

import com.theapache64.tgb.data.local.base.AddQueryBuilder
import com.theapache64.tgb.data.local.base.BaseTable
import com.theapache64.tgb.data.local.base.SelectQueryBuilder
import com.theapache64.tgb.data.local.base.UpdateQueryBuilder
import com.theapache64.tgb.models.Hit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HitsRepo @Inject constructor() : BaseTable<Hit>("hits") {

    companion object {
        private const val COLUMN_USER = "user"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_GIF_ID = "gif_id"
        private const val COLUMN_FILE_ID = "file_id"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_IS_SUCCESS = "is_success"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"
        private const val COLUMN_WIDTH = "width"
        private const val COLUMN_HEIGHT = "height"
        private const val COLUMN_TRY_COUNT = "try_count"
    }

    override fun add(newInstance: Hit): Boolean {
        return AddQueryBuilder.Builder(tableName)
                .add(COLUMN_USER, newInstance.user)
                .add(COLUMN_USER_ID, newInstance.userId)
                .add(COLUMN_GIF_ID, newInstance.gifId)
                .add(COLUMN_FILE_ID, newInstance.fileId)
                .add(COLUMN_WIDTH, newInstance.width)
                .add(COLUMN_HEIGHT, newInstance.height)
                .done()
    }

    fun getByGifId(gifId: String): Hit? {
        return getItem(COLUMN_GIF_ID, gifId)
    }

    override fun getItem(column: String, value: String): Hit? {
        return getSelectHitBuilder().where(column, value).build().get()
    }

    override fun update(t: Hit): Boolean {
        return UpdateQueryBuilder.Builder(tableName)
                .set(COLUMN_TEXT, t.text)
                .set(COLUMN_IS_SUCCESS, t.isSuccess ?: false)
                .where(COLUMN_ID, t.id.toString())
                .build()
                .done()
    }

    fun getLastHit(userId: Long): Hit? {
        return getSelectHitBuilder()
                .orderBy("$COLUMN_ID DESC")
                .where(COLUMN_USER_ID, userId.toString()).build().get()
    }

    private fun getSelectHitBuilder(): SelectQueryBuilder.Builder<Hit> {
        return SelectQueryBuilder.Builder(tableName) {
            Hit(
                    it.getLong(COLUMN_ID),
                    it.getString(COLUMN_USER),
                    it.getLong(COLUMN_USER_ID),
                    it.getString(COLUMN_GIF_ID),
                    it.getString(COLUMN_FILE_ID),
                    it.getInt(COLUMN_WIDTH),
                    it.getInt(COLUMN_HEIGHT),
                    it.getInt(COLUMN_TRY_COUNT),
                    it.getString(COLUMN_TEXT),
                    it.getBoolean(COLUMN_IS_SUCCESS),
                    it.getString(COLUMN_CREATED_AT),
                    it.getString(COLUMN_UPDATED_AT)
            )

        }.select(arrayOf(
                COLUMN_ID,
                COLUMN_USER,
                COLUMN_USER_ID,
                COLUMN_GIF_ID,
                COLUMN_FILE_ID,
                COLUMN_TEXT,
                COLUMN_WIDTH,
                COLUMN_HEIGHT,
                COLUMN_TRY_COUNT,
                COLUMN_IS_SUCCESS,
                COLUMN_CREATED_AT,
                COLUMN_UPDATED_AT
        ))
    }
}