package com.wafflestudio.msns.global.util

import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom

@Service
class RandomUtil {
    fun generateRandomCode(length: Int): String {
        val code = StringBuilder()
        for (i in 1..length) {
            code.append(random.nextInt(10))
        }
        return code.toString()
    }

    companion object {
        private val random = ThreadLocalRandom.current()
    }
}
