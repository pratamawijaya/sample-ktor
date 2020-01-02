package com.pratama.domain.model

import java.util.*

data class User(val email: String, val password: String)

val users = Collections.synchronizedMap(
    listOf(User("test@mail.com", "test"))
        .associateBy { it.email }
        .toMutableMap()
)

data class LoginRequest(val email:String, val password: String)