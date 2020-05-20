package com.theapache64.tgb.servlets

import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import com.theapache64.telegrambot.api.data.remote.repos.TelegramRepo
import com.theapache64.telegrambot.api.models.SendMessageRequest
import com.theapache64.telegrambot.api.models.Update
import com.theapache64.tgb.data.local.HitsRepo
import com.theapache64.tgb.models.Hit
import com.theapache64.tgb.utils.FfmpegUtils
import javax.inject.Inject

class TgbViewModel @Inject constructor(
        private val telegramRepo: TelegramRepo,
        private val hitsRepo: HitsRepo
) : BaseViewModel<TgbServlet>() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String> = _response

    private var update: Update? = null

    fun init(jsonUpdate: String) {
        this.update = telegramRepo.parseUpdate(jsonUpdate)
        println("Servlet got hit from ${update?.message?.from?.firstName},${update?.message?.from?.username}")
    }

    override suspend fun call(command: TgbServlet): Int {

        if (update != null) {

            // Smart case enabled
            update?.let { update ->

                // Valid update
                when {

                    update.message.animation != null -> {
                        // It's a GIF
                        println("It's a GIF")

                        // Send a reply action
                        askText(update)
                    }

                    update.message.text != null && update.message.replyToMessage != null -> {
                        // It's the text to write on top of gif
                        val text = update.message.text
                        if (!text.isNullOrBlank()) {
                            println("It's the text to write on top of GIF : $text")
                            val replyMsgId = update.message.replyToMessage!!.messageId
                            val hit = hitsRepo.getByMessageId(replyMsgId)
                            if (hit != null) {
                                val gifFile = telegramRepo.downloadGif(hit.fileId)
                                if (gifFile != null) {
                                    val newMp4File = FfmpegUtils.draw(text, hit.width, hit.height, gifFile, false)
                                } else {
                                    sendInvalidRequest(update, """
                                Sorry, we couldn't find your GIF in Telegram database.Please try again.
                            """.trimIndent())
                                }
                            } else {
                                sendInvalidRequest(update, """
                                Sorry, we couldn't find your GIF in our database!.Please try again.
                            """.trimIndent())
                            }
                        } else {
                            sendInvalidRequest(update, """
                                Sorry, we couldn't find your GIF in our database!.Please try again.
                            """.trimIndent())
                        }
                    }

                    else -> {
                        // This response can't be handled
                        sendInvalidRequest(update)
                        _response.value = "Invalid request"
                    }
                }
            }
        } else {
            // We messed up the model here
            _response.value = "Couldn't parse update!"
        }

        return 0
    }

    private suspend fun askText(update: Update) {

        println("Asking text...")
        val askMsg = telegramRepo.sendMessage(
                SendMessageRequest(
                        update.message.chat.id,
                        "Awesome GIF! Now tell me what do you want to write on it!",
                        replyMsgId = update.message.messageId,
                        replyMarkup = SendMessageRequest.ReplyMarkup(
                                isForceReply = true
                        )
                )
        )

        val from = update.message.from
        var user = if (from.username.isNullOrBlank()) {
            from.firstName
        } else {
            from.username
        }

        if (user.isNullOrBlank()) {
            user = "Unknown"
        }

        val gif = update.message.animation!!
        val hit = Hit(
                null,
                user,
                askMsg.result!!.messageId,
                update.message.animation!!.fileId,
                gif.width,
                gif.height,
                null,
                null,
                null,
                null)

        if (hitsRepo.add(hit)) {
            _response.value = "Hit added to DB"
        } else {
            _response.value = "Failed to add hit to db"
        }
    }

    private suspend fun sendInvalidRequest(
            update: Update,
            message: String = "Ahhm.. That's an invalid query."
    ) {
        telegramRepo.sendMessage(
                SendMessageRequest(
                        update.message.chat.id,
                        message,
                        replyMsgId = update.message.messageId
                )
        )
    }
}