package com.wafflestudio.msns.global.auth.dto

import com.wafflestudio.msns.global.enum.Verify

class LoginRequest {
    val account: String = ""
    val password: String = ""
    val type: Verify = Verify.NONE
}
