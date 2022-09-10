package com.wafflestudio.msns.global.auth.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.InvalidRequestException

class JWTExpiredException(detail: String = "") :
    InvalidRequestException(ErrorType.EXPIRED_JWT, detail)
