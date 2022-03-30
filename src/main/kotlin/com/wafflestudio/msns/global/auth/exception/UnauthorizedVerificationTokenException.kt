package com.wafflestudio.msns.global.auth.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.NotAllowedException

class UnauthorizedVerificationTokenException(detail: String = "") :
    NotAllowedException(ErrorType.UNAUTHORIZED_EMAIL, detail)
