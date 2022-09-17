package com.wafflestudio.msns.global.exception

enum class ErrorType(
    val code: Int
) {
    INVALID_REQUEST(0),
    INVALID_POST_TITLE(1),

    NOT_ALLOWED(3000),
    UNAUTHORIZED_EMAIL(3001),
    FORBIDDEN_TOKEN(3002),
    FORBIDDEN_USERNAME(3003),

    DATA_NOT_FOUND(4000),
    BAD_REQUEST(4001),
    TOKEN_NOT_FOUND(4002),
    INVALID_JWT(4003),
    INVALID_CODE(4004),
    ALREADY_EXISTS_USER(4005),
    INVALID_EMAIL_FORM(4006),
    EXPIRED_JWT(4007),
    INVALID_BIRTH_FORM(4008),

    CONFLICT(9000),

    SERVER_ERROR(10000),
}
