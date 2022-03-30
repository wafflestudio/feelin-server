package com.wafflestudio.msns.global.auth.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.InvalidRequestException

class InvalidEmailFormException(detail: String = "") :
    InvalidRequestException(ErrorType.INVALID_EMAIL_FORM, detail)
