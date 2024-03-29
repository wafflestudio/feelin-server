package com.wafflestudio.msns.global.exception

enum class ErrorType(
    val code: Int
) {
    INVALID_REQUEST(0),
    INVALID_POST_TITLE(1),
    INVALID_SIGN_IN(2),
    INVALID_BIRTH_FORM(3),

    NOT_ALLOWED(3000),
    UNAUTHORIZED_EMAIL(3001),
    FORBIDDEN_TOKEN(3002),
    FORBIDDEN_USERNAME(3003),
    FORBIDDEN_USER_DELETE_POST(3004),
    FORBIDDEN_FOLLOW(3005),
    FORBIDDEN_USER_PUT_POST(3006),
    FORBIDDEN_DELETE_FOLLOW(3007),
    COMMUNICATING_SERVER_AUTHORIZATION(3008),
    FORBIDDEN_BLOCK(3009),

    DATA_NOT_FOUND(4000),
    BAD_REQUEST(4001),
    TOKEN_NOT_FOUND(4002),
    INVALID_JWT(4003),
    INVALID_CODE(4004),
    INVALID_EMAIL_FORM(4006),
    EXPIRED_JWT(4007),
    INVALID_PLAYLIST_ORDER(4010),
    INVALID_SIGNUP_FORM(4011),

    CONFLICT(9000),
    ALREADY_EXISTS_FOLLOW(9001),
    ALREADY_EXISTS_BLOCK(9002),
    ALREADY_EXISTS_USER(9003),

    SERVER_ERROR(10000),
}
