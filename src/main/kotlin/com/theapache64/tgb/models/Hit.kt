package com.theapache64.tgb.models

class Hit(
        val id: Long?,
        val user: String,
        val messageId: Long,
        val fileId: String,
        val width: Int,
        val height: Int,
        val text: String?,
        val isSuccess: Boolean?,
        val createdAt: String?,
        val updatedAt: String?
)