package com.wafflestudio.msns.global.exception

import java.lang.RuntimeException

abstract class FeelinException(val errorType: ErrorType, val detail: String = "") :
    RuntimeException(errorType.name)

abstract class InvalidRequestException(errorType: ErrorType, detail: String = "") :
    FeelinException(errorType, detail)

abstract class DataNotFoundException(errorType: ErrorType, detail: String = "") :
    FeelinException(errorType, detail)

abstract class NotAllowedException(errorType: ErrorType, detail: String = "") :
    FeelinException(errorType, detail)

abstract class ConflictException(errorType: ErrorType, detail: String = "") :
    FeelinException(errorType, detail)
