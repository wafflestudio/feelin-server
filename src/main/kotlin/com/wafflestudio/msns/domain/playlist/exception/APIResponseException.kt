package com.wafflestudio.msns.domain.playlist.exception

import com.wafflestudio.msns.global.exception.DataNotFoundException
import com.wafflestudio.msns.global.exception.ErrorType

class APIResponseException(detail: String = "") :
    DataNotFoundException(ErrorType.DATA_NOT_FOUND, detail)
