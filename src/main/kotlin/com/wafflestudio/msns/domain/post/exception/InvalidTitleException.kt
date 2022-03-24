package com.wafflestudio.msns.domain.post.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.InvalidRequestException

class InvalidTitleException(detail: String = "") :
    InvalidRequestException(ErrorType.INVALID_POST_TITLE, detail)
