package com.theapache64.tgb.servlets

import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import com.theapache64.telegrambot.api.data.remote.repos.TelegramRepo
import com.theapache64.telegrambot.api.models.SendChatActionRequest
import com.theapache64.telegrambot.api.models.SendMessageRequest
import com.theapache64.telegrambot.api.models.SendPhotoRequest
import com.theapache64.telegrambot.api.models.Update
import com.theapache64.tgb.data.local.HitsRepo
import com.theapache64.tgb.models.Hit
import com.theapache64.tgb.core.GifMaster
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
                        val text = update.message.text!!.trim()

                        // Blank check
                        if (text.isEmpty()) {
                            sendInvalidRequest(update, """
                                Are you kidding me?
                            """.trimIndent())
                            return@let
                        }

                        println("It's the text to write on top of GIF : $text")

                        val replyMsgId = update.message.replyToMessage!!.messageId
                        var hit = hitsRepo.getByMessageId(replyMsgId)

                        if (hit == null) {
                            sendInvalidRequest(update, """
                                Sorry, we couldn't find your GIF in our database!.Please try again.
                            """.trimIndent())
                            return@let
                        }

                        val gifFile = telegramRepo.downloadGif(hit.fileId)

                        if (gifFile == null) {
                            sendInvalidRequest(update, """
                                Sorry, we couldn't find your GIF in Telegram database.Please try again.
                            """.trimIndent())
                            return@let
                        }


                        val chatId = update.message.chat.id
                        telegramRepo.sendChatActionAsync(SendChatActionRequest(
                                TelegramRepo.CHAT_ACTION_SENDING_PHOTO,
                                chatId
                        ))

                        val newMp4File = GifMaster.draw(text, gifFile, false)
                        gifFile.delete()
                        if (newMp4File == null) {
                            sendInvalidRequest(update, """
                                Sorry. We couldn't process that. 
                                <a href="https://github.com/theapache64/txtgifbot/issues/new">Please report it here</a>
                            """.trimIndent())
                            // Updating DB
                            hit = hit.apply {
                                this.text = text
                                this.isSuccess = false
                            }

                        } else {
                            println("output: file://${newMp4File.absolutePath}")

                            // Updating DB
                            hit = hit.apply {
                                this.text = text
                                this.isSuccess = true
                            }

                            // Sending result
                            telegramRepo.sendAnimation(
                                    chatId,
                                    newMp4File
                            )

                            newMp4File.delete()
                        }

                        hitsRepo.update(hit)
                        return@let
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
            message: String = "What? I don't know about that. Send me a GIF!!!"
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