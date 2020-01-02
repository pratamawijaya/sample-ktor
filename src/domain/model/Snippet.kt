package com.pratama.domain.model

import java.util.*

data class Snippet(val text: String)

val snippets = Collections.synchronizedList(
    mutableListOf(
        Snippet("Hello"),
        Snippet("World")
    )
)