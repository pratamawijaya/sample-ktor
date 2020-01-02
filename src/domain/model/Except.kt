package com.pratama.domain.model

import java.lang.RuntimeException

class InvalidCredentialsException(val msg: String) : RuntimeException(msg)