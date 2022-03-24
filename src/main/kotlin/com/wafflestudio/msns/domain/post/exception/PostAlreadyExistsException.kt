package com.wafflestudio.msns.domain.post.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.NotAllowedException

class PostAlreadyExistsException(detail: String = "") :
    NotAllowedException(ErrorType.NOT_ALLOWED, detail)
