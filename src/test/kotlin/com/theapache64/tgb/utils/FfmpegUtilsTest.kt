package com.theapache64.tgb.utils

import org.junit.Test
import java.io.File
import java.lang.StringBuilder

class FfmpegUtilsTest {

    private val bowMp4File = File("src/test/resources/bow.mp4")
    private val bowSmallMp4File = File("src/test/resources/bow_small.mp4")

    @Test
    fun `Draw text with different length`() {

        val texts = arrayOf(
                "THANKS!",
                "ABCDEFGHIJKLMNOPQ",
                "Thank you so much man. \n I really appreciate this"
        )

        val gifFilePaths = mutableListOf<String>()

        for (text in texts) {
            val newFile = FfmpegUtils.draw(text, 480, 376, bowMp4File, true)
            gifFilePaths.add("""
                <img src="${newFile!!.absolutePath}.gif"/>
            """.trimIndent())
        }

        gifFilePaths.add("<br/>")
        for (text in texts) {
            val newFile = FfmpegUtils.draw(text, 240, 188, bowSmallMp4File, true)
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