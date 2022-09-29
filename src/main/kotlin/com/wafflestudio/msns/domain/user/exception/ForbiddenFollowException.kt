package com.wafflestudio.msns.domain.user.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.NotAllowedException

class ForbiddenFollowException(detail: String = "") :
    NotAllowedException(ErrorType.FORBIDDEN_FOLLOW, detail)
