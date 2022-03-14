package com.wafflestudio.msns.domain.user.api

import com.wafflestudio.msns.domain.user.service.UserService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
)
