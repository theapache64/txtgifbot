package com.theapache64.tgb.models

class Hit(
        val id: Long?,
        val user: String,
        val userId: Long,
        val gifId: String,
        val fileId: String,
        val width: Int,
        val height: Int,
        var tryCount: Int,
        var text: String?,
        var isSuccess: Boolean?,
        val createdAt: String?,
        val updatedAt: String?
)