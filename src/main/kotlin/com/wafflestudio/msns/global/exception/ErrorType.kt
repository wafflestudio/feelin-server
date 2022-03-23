package com.wafflestudio.msns.global.exception

enum class ErrorType(
    val code: Int
) {
    INVALID_REQUEST(0),

    NOT_ALLOWED(3000),

    DATA_NOT_FOUND(4000),
    BAD_REQUEST(4001),
    TOKEN_NOT_FOUND(4002),
    INVALID_JWT(4003),
    INVALID_CODE(4004),
    ALREADY_EXISTS_USERNAME(4005),
    UNAUTHORIZED_EMAIL(4006),
    INVALID_EMAIL_FORM(4007),

    CONFLICT(9000),

    SERVER_ERROR(10000),
}
