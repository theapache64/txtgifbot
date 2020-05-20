package com.theapache64.tgb.utils

import com.winterbe.expekt.should
import org.junit.Test


class StringUtilsTest {
    @Test
    fun `File name`() {
        StringUtils.apply {
            toFileName("file name").should.equal("file_name")
            toFileName("file                name").should.equal("file_name")
            toFileName("file123!name").should.equal("file123_name")
        }
    }
}