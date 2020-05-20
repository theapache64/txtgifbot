package com.theapache64.tgb.servlets

import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import com.theapache64.telegrambot.api.data.remote.repos.TelegramRepo
import com.theapache64.telegrambot.api.models.SendMessageRequest
import com.theapache64.telegrambot.api.models.Update
import com.theapache64.tgb.data.local.HitsRepo
import com.theapache64.tgb.models.Hit
import com.theapache64.tgb.core.GifMaster
import javax.inject.Inject

class TgbViewModel @Inject constructor(
        private val telegramRepo: TelegramRepo,
        private val hitsRepo: HitsRepo
) : BaseViewModel<TgbServlet>() {

    companion object {
        private const val MAX_TEXT_LENGTH = 17
    }

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

                        // Black check
                        if (text.isEmpty()) {
                            sendInvalidRequest(update, """
                                Are you kidding me?
                            """.trimIndent())
                            return@let
                        }

                        // Length check
                        if (text.length > MAX_TEXT_LENGTH) {
                            sendInvalidRequest(update, """
                                Sorry, I can't handle more than $MAX_TEXT_LENGTH chars.
                                May be you should <a href="https://github.com/theapache64/txtgifbot/issues/2"> vote for it</a>
                            """.trimIndent())
                            return@let
                        }

                        // Multiline check
                        if (text.contains("\n")) {
                            sendInvalidRequest(update, """
                                Sorry, I can't handle multiline.
                                May be you should <a href="https://github.com/theapache64/txtgifbot/issues/1"> vote for it</a>
                            """.trimIndent())
                            return@let
                        }

                        println("It's the text to write on top of GIF : $text")

                        val replyMsgId = update.message.replyToMessage!!.messageId
                        val hit = hitsRepo.getByMessageId(replyMsgId)

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


                        val newMp4File = GifMaster.draw(text, hit.width, hit.height, gifFile, false)
                        gifFile.delete()
                        if (newMp4File == null) {
                            sendInvalidRequest(update, """
                                Sorry. We couldn't process that. 
                                <a href="https://github.com/theapache64/txtgifbot/issues/new">Please report it here</a>
                            """.trimIndent())
                            return@let
                        }
                        println("output: file://${newMp4File.absolutePath}")

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