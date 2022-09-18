package com.wafflestudio.msns.domain.playlist.exception

import com.wafflestudio.msns.global.exception.ConflictException
import com.wafflestudio.msns.global.exception.ErrorType

class InternalServerException(detail: String = "") :
    ConflictException(ErrorType.SERVER_ERROR, detail)
