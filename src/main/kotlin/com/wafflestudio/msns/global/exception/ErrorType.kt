package com.wafflestudio.msns.global.exception

enum class ErrorType(
    val code: Int
) {
    INVALID_REQUEST(0),
    INVALID_POST_TITLE(1),

    NOT_ALLOWED(3000),

    DATA_NOT_FOUND(4000),
    BAD_REQUEST(4001),
    TOKEN_NOT_FOUND(4002),
    INVALID_JWT(4003),
    INVALID_CODE(4004),


    CONFLICT(9000),

    SERVER_ERROR(10000),
}
