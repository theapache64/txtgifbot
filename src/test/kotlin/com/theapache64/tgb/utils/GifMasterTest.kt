package com.theapache64.tgb.utils

import com.theapache64.tgb.core.GifMaster
import org.junit.Test
import java.io.File

class GifMasterTest {

    private val bowMp4File = File("src/test/resources/bow.mp4")
    private val bowSmallMp4File = File("src/test/resources/bow_small.mp4")

    @Test
    fun `Draw multiline text`() {
        val text = "A\nB\nC".trimIndent()

        val gifFilePaths = mutableListOf<String>()

        val newFile = GifMaster.draw(text, bowMp4File, true)
        gifFilePaths.add("""
                <img src="${newFile!!.absolutePath}.gif"/>
            """.trimIndent())


        val html = """
            <html>
                <body>
                    ${gifFilePaths.joinToString(" ")}
                </body>
            </html>
        """.trimIndent()
        File("src/test/resources/index.html").writeText(html)
    }

    @Test
    fun `Draw text with different length`() {

        val texts = arrayOf(
                "THANKS!",
                "ABCDEFGHIJKLMNOPQ",
                "Thank you so much man. I really appreciate this"
        )

        val gifFilePaths = mutableListOf<String>()

        for (text in texts) {
            val newFile = GifMaster.draw(text, bowMp4File, true)
            gifFilePaths.add("""
                <img src="${newFile!!.absolutePath}.gif"/>
            """.trimIndent())
        }

        gifFilePaths.add("<br/>")
        for (text in texts) {
            val newFile = GifMaster.draw(text, bowSmallMp4File, true)
            gifFilePaths.add("""
                <img src="${newFile!!.absolutePath}.gif"/>
            """.trimIndent())
        }

        val html = """
            <html>
                <body>
                    ${gifFilePaths.joinToString(" ")}
                </body>
            </html>
        """.trimIndent()
        File("src/test/resources/index.html").writeText(html)
    }
}