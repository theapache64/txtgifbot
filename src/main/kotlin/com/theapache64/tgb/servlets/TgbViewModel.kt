package com.theapache64.tgb.servlets

import com.squareup.moshi.Moshi
import com.theapache64.cyclone.core.base.BaseViewModel
import com.theapache64.cyclone.core.livedata.LiveData
import com.theapache64.cyclone.core.livedata.MutableLiveData
import com.theapache64.telegrambot.api.data.remote.repos.TelegramRepo
import com.theapache64.telegrambot.api.models.SendAnimationRequest
import com.theapache64.telegrambot.api.models.SendChatActionRequest
import com.theapache64.telegrambot.api.models.SendMessageRequest
import com.theapache64.telegrambot.api.models.Update
import com.theapache64.tgb.data.local.HitsRepo
import com.theapache64.tgb.models.Hit
import com.theapache64.tgb.core.GifMaster
import com.theapache64.tgb.utils.SecretConstants
import javax.inject.Inject

class TgbViewModel @Inject constructor(
        private val telegramRepo: TelegramRepo,
        private val hitsRepo: HitsRepo,
        private val moshi: Moshi
) : BaseViewModel<TgbServlet>() {

    companion object {
        private val GIF_COMPLEMENTS = listOf<String>(
                "🤘🏻 Awesome GIF!",
                "😂 Haha, that's cool",
                "😹 Okay nice",
                "😎 Haha, cool"
        )
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

                    update.message.animation != null || update.message.video != null -> {
                        askText(update)
                    }

                    update.message.text != null -> {
                        // Checking if the text is reply to some GIF
                        val replyAnimation = update.message.replyToMessage?.animation
                                ?: update.message.replyToMessage?.video

                        val text = update.message.text!!.trim()

                        if (replyAnimation != null) {

                            // Text came as reply to GIF.
                            val hit = hitsRepo.getByGifId(replyAnimation.fileUniqueId)
                            if (hit == null) {
                                sendInvalidRequest(update, "That gif doesn't exist in our server. Please send the GIF again")
                                return@let
                            }

                            sendGif(text, hit, update)

                        } else {

                            if (text.isNotEmpty()) {

                                // gif or video
                                val lastHit = hitsRepo.getLastHit(update.message.from.id)

                                if (lastHit == null) {
                                    sendInvalidRequest(update, "Send me a <b>GIF</b> first")
                                    return@let
                                }

                                // Last gif available
                                sendGif(text, lastHit, update)
                            } else {
                                sendInvalidRequest(update, "What should I do with blank text? 🤷 Send me some text")
                            }
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

    private suspend fun sendGif(text: String, _hit: Hit, update: Update) {

        var hit = _hit

        val chatId = update.message.chat.id
        val resp = telegramRepo.sendChatAction(SendChatActionRequest(
                TelegramRepo.CHAT_ACTION_SENDING_DOCUMENT,
                chatId
        ))

        println("Sending file sent! $resp")

        val gifFile = telegramRepo.downloadGif(hit.fileId)

        if (gifFile == null) {
            sendInvalidRequest(update, """
                               🤒 Sorry, we couldn't find your GIF in Telegram database.Please try again.
                            """.trimIndent())
            return
        }


        val newMp4File = GifMaster.draw(text, gifFile, false)
        gifFile.delete()

        if (newMp4File == null) {
            sendInvalidRequest(update, """
                               🤒 Sorry. We couldn't process that. 
                                <a href="https://github.com/theapache64/txtgifbot/issues/new">Please report it here</a>
                            """.trimIndent())
            // Updating DB
            hit = hit.apply {
                this.text = text
                this.isSuccess = false
                this.tryCount = this.tryCount + 1
            }

        } else {
            println("output: file://${newMp4File.absolutePath}")

            // Updating DB
            hit = hit.apply {
                this.text = text
                this.isSuccess = true
            }

            // Sending result
            val sendResult = telegramRepo.sendAnimation(
                    chatId,
                    newMp4File
            )

            // Statistics
            telegramRepo.sendAnimation(
                    SendAnimationRequest(
                            sendResult.result.document.fileId,
                            "@${hit.user} just created a GIF!",
                            SecretConstants.STATS_GROUP
                    )
            )

            newMp4File.delete()
        }

        hitsRepo.update(hit)
    }

    private suspend fun askText(update: Update) {

        telegramRepo.sendMessage(
                SendMessageRequest(
                        update.message.chat.id,
                        "${getRandomGifComplement()}. Now tell me what do you want to write on it!"
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

        val gif = (update.message.animation ?: update.message.video)!!
        val hit = Hit(
                null,
                user,
                from.id,
                gif.fileUniqueId,
                gif.fileId,
                gif.width,
                gif.height,
                0,
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

    private fun getRandomGifComplement(): String = GIF_COMPLEMENTS.random()

    private suspend fun sendInvalidRequest(
            update: Update,
            message: String = "🤷‍♂️ What? I don't know about that. <b>Send me a GIF!!!</b>"
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