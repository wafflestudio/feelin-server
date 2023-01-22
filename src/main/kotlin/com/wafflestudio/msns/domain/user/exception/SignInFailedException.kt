package com.wafflestudio.msns.domain.user.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.InvalidRequestException

class SignInFailedException(detail: String = "") :
    InvalidRequestException(ErrorType.INVALID_SIGN_IN, detail)
