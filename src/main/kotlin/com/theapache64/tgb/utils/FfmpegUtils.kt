package com.theapache64.tgb.utils

import java.io.File
import kotlin.math.sqrt

object FfmpegUtils {

    private const val fontPath = "assets/impact.ttf"
    private const val TEXT_SIZE = 50
    private const val WIDTH = 480
    private const val HEIGHT = 376
    private val DIAGONAL = sqrt((WIDTH * WIDTH) + (HEIGHT * HEIGHT).toDouble())

    fun draw(
            text: String,
            width: Int,
            height: Int,
            gifFile: File,
            isCreateGif: Boolean
    ): File? {

        val outputFile = File("${gifFile.parent}/${StringUtils.toFileName("${text}_${gifFile.name}")}")
        var textSize = (sqrt((width * width + height * height).toDouble()) / DIAGONAL * TEXT_SIZE).toInt()
        val bottomMargin = height * 0.02

        require(textSize > 0) { "Text size can't be <= 0" }

        val command = """
            ffmpeg -y -i "${gifFile.absolutePath}" -vf "
                drawtext=
                  text=$text:
                  fontcolor=white:
                  fontsize=$textSize:
                  x=(w/2)-(tw/2):
                  y=h-th-$bottomMargin:
                  borderw=3:
                  bordercolor=black:
                  fontfile=$fontPath
                  " "${outputFile.absolutePath}" 
        """.trimIndent()

        SimpleCommandExecutor.executeCommand(
                command,
                isLivePrint = false,
                isSuppressError = true,
                isReturnAll = false
        )

        if (outputFile.exists() && isCreateGif) {
            val gifCommand = """ffmpeg -y -i "${outputFile.absolutePath}" -filter_complex "[0:v] fps=12,scale=480:-2,split [a][b];[a] palettegen [p];[b][p] paletteuse" "${outputFile.absolutePath}.gif" """
            SimpleCommandExecutor.executeCommand(
                    gifCommand,
                    isLivePrint = true,
                    isSuppressError = true,
                    isReturnAll = false
            )
        }

        return outputFile
    }


}