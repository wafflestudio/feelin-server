package com.wafflestudio.msns.global.enum

enum class ENV(val domain: String) {
    LOCAL("https://feelin-api-dev.wafflestudio.com/api/v1"),
    DEV("https://feelin-api-dev.wafflestudio.com/api/v1"),
    PROD("https://feelin-api.wafflestudio.com/api/v1")
}
