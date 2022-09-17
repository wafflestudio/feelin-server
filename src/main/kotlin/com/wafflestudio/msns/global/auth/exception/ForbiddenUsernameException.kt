package com.wafflestudio.msns.global.auth.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.NotAllowedException

class ForbiddenUsernameException(detail: String = "") :
    NotAllowedException(ErrorType.FORBIDDEN_USERNAME, detail)
