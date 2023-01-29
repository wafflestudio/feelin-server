package com.wafflestudio.msns.domain.user.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.NotAllowedException

class ForbiddenBlockException(detail: String = "") :
    NotAllowedException(ErrorType.FORBIDDEN_BLOCK, detail)
