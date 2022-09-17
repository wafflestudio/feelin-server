package com.wafflestudio.msns.global.auth.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.InvalidRequestException

class InvalidBirthFormException(detail: String = "") :
    InvalidRequestException(ErrorType.INVALID_BIRTH_FORM, detail)
