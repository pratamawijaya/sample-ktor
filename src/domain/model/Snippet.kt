package com.pratama.domain.model

import java.util.*

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

data class Snippet(val text: String)

val snippets = Collections.synchronizedList(
    mutableListOf(
        Snippet("Hello"),
        Snippet("World")
    )
)