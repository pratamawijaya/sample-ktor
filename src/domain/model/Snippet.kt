package com.pratama.domain.model

import java.util.*

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

data class Snippet(val user: String, val text: String)

val snippets = Collections.synchronizedList(
    mutableListOf(
        Snippet(user = "test@mail.com", text = "Hello"),
        Snippet(user = "test@mail.com", text = "World")
    )
)