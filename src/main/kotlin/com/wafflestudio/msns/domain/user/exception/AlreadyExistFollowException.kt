package com.wafflestudio.msns.domain.user.exception

import com.wafflestudio.msns.global.exception.ConflictException
import com.wafflestudio.msns.global.exception.ErrorType

class AlreadyExistFollowException(detail: String = "") :
    ConflictException(ErrorType.ALREADY_EXISTS_FOLLOW, detail)
