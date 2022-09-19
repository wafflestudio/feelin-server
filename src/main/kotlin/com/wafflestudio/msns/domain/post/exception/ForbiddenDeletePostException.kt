package com.wafflestudio.msns.domain.post.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.NotAllowedException

class ForbiddenDeletePostException(detail: String = "") :
    NotAllowedException(ErrorType.FORBIDDEN_USER_DELETE_POST, detail)
