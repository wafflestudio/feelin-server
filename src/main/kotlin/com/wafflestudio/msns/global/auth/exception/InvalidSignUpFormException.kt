package com.wafflestudio.msns.global.auth.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.InvalidRequestException

class InvalidSignUpFormException(detail: String = "") :
    InvalidRequestException(ErrorType.INVALID_SIGNUP_FORM, detail)
