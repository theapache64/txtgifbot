package com.theapache64.tgb.servlets

import com.theapache64.telegrambot.api.di.modules.TelegramModule
import com.theapache64.tgb.utils.SecretConstants
import com.theapache64.tgb.utils.runBlockingUnit
import java.lang.Exception
import javax.inject.Inject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(urlPatterns = ["/gen_gif"])
class TgbServlet : HttpServlet() {

    @Inject
    lateinit var viewModel: TgbViewModel


    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) = runBlockingUnit {

        try
        {
            DaggerTgbComponent.builder()
                    .telegramModule(TelegramModule(SecretConstants.TXT_GIF_BOT_TOKEN))
                    .build()
                    .inject(this)

            val updateJson = req.reader.readText()
            viewModel.init(updateJson)

            viewModel.response.observe { message ->
                println("SERVER : $message")
                resp.writer.write(message)
            }

            viewModel.call(this@TgbServlet)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}