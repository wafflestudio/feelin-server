package com.wafflestudio.msns.domain.user.exception

import com.wafflestudio.msns.global.exception.DataNotFoundException
import com.wafflestudio.msns.global.exception.ErrorType

class UserNotFoundException(detail: String = "") :
    DataNotFoundException(ErrorType.DATA_NOT_FOUND, detail)
