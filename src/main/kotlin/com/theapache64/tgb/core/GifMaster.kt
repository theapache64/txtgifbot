package com.theapache64.tgb.core

import com.theapache64.telegrambot.api.utils.StringUtils
import com.theapache64.tgb.utils.SimpleCommandExecutor
import java.io.File

object GifMaster {

    private const val TEXT_SIZE = 45

    fun draw(
            text: String,
            gifFile: File,
            isCreateGif: Boolean
    ): File? {

        val outputFile = File("${gifFile.parent}/${StringUtils.toFileName("${text}_${gifFile.name}")}.mp4")
        val subTitleFile = createSubTitleFile(gifFile, text)

        val command = """
            ffmpeg -y -i "${gifFile.absolutePath}" -vf "    
                subtitles='${subTitleFile.absolutePath}':
                force_style='Fontname=Impact,Fontsize=$TEXT_SIZE,PrimaryColour=&Hffffff&,OutlineColour=&H000000&,BorderStyle=1,Outline=2'" \
                "${outputFile.absolutePath}"
        """.trimIndent()

        SimpleCommandExecutor.executeCommand(
                command,
                isLivePrint = false,
                isSuppressError = true,
                isReturnAll = false
        )

        if (outputFile.exists() && isCreateGif) {

            // DEBUG PURPOSE ONLY
            val gifCommand = """
                   ffmpeg -y -i "${outputFile.absolutePath}" -filter_complex "
                       [0:v] fps=12,scale=480:-2,split [a][b];
                       [a] palettegen [p];[b][p] paletteuse" "${outputFile.absolutePath}.gif"
                """.trimIndent()

            SimpleCommandExecutor.executeCommand(
                    gifCommand,
                    isLivePrint = false,
                    isSuppressError = true,
                    isReturnAll = false
            )
        }

        subTitleFile.delete()

        return outputFile
    }

    private fun createSubTitleFile(input: File, text: String): File {
        val content = """
            1
            00:00:00,000 --> 99:99:99,999
            $text
        """.trimIndent()
        return File("${input.parent}/${input.nameWithoutExtension}.srt").apply {
            writeText(content)
        }
    }


}