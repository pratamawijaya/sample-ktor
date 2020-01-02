package com.pratama

import com.fasterxml.jackson.databind.SerializationFeature
import com.pratama.domain.model.PostSnippet
import com.pratama.domain.model.Snippet
import com.pratama.domain.model.snippets
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.text.DateFormat

fun main(args: Array<String>) {
    embeddedServer(
            Netty,
            port = 3030,
            host = "localhost",
            module = Application::mainModule
    )
            .start(wait = true)
}

fun Application.mainModule() {
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateInstance()
        }
    }

    routing {
        root()
    }
}

fun Routing.root() {
    get("/healthcheck") {
        call.respondText("OK")
    }

    get("/") {
        call.respondText("Hello World")
    }

    get("/snippets") {
        call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
    }

    post("/snippets") {
        val post = call.receive<PostSnippet>()
        snippets += Snippet(post.snippet.text)
        call.respond(mapOf("Success" to true))
    }

    get("/profile") {
        call.respondText(
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.OK,
                text = "user"
        )
    }
}