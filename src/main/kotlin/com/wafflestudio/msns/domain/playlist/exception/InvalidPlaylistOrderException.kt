package com.wafflestudio.msns.domain.playlist.exception

import com.wafflestudio.msns.global.exception.ErrorType
import com.wafflestudio.msns.global.exception.InvalidRequestException

class InvalidPlaylistOrderException(detail: String = "") :
    InvalidRequestException(ErrorType.INVALID_PLAYLIST_ORDER, detail)
