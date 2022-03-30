package com.wafflestudio.msns.domain.post.exception

import com.wafflestudio.msns.global.exception.DataNotFoundException
import com.wafflestudio.msns.global.exception.ErrorType

class PostNotFoundException(detail: String = "") :
    DataNotFoundException(ErrorType.DATA_NOT_FOUND, detail)
