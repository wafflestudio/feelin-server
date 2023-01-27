package com.wafflestudio.msns.domain.playlist.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.NotAllowedException

class WithdrawNotAllowedException(detail: String = "") :
    NotAllowedException(ErrorType.COMMUNICATING_SERVER_AUTHORIZATION, detail)
