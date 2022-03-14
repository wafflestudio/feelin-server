package com.wafflestudio.msns.global.auth.exception

import com.wafflestudio.msns.global.exception.DataNotFoundException
import com.wafflestudio.msns.global.exception.ErrorType

class VerificationTokenNotFoundException(detail: String = "") :
    DataNotFoundException(ErrorType.TOKEN_NOT_FOUND, detail)
