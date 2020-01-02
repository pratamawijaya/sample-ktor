package com.pratama

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import com.pratama.domain.model.*
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
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

open class SimpleJWT(private val secret: String) {
    private val algo = Algorithm.HMAC256(secret)
    val verifier: JWTVerifier = JWT.require(algo).build()
    fun sign(email: String): String = JWT.create().withClaim("email", email).sign(algo)
}

fun Application.mainModule() {
    val simpleJWT = SimpleJWT("cobajwtsupersecret")

    install(Authentication) {
        jwt {
            verifier(simpleJWT.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("email").asString())
            }
        }
    }

    install(StatusPages) {
        exception<Throwable> {
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
        root(simpleJWT)
    }
}

fun Routing.root(simpleJWT: SimpleJWT) {
    get("/healthcheck") {
        call.respondText("OK")
    }

    get("/") {
        call.respondText("Hello World")
    }

    post("/login") {
        val post = call.receive<LoginRequest>()
        val user = users.getOrPut(post.email) { User(post.email, post.password) }

        if (user.password != post.password) error("Invalid credentials")
        call.respond(mapOf("token" to simpleJWT.sign(user.email)))
    }

    route("/snippets") {
        get {
            call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
        }

        authenticate {
            post {
                val post = call.receive<PostSnippet>()
                snippets += Snippet(post.snippet.text)
                call.respond(mapOf("Success" to true))
            }
        }
    }

    get("/profile") {
        call.respondText(
            contentType = ContentType.Application.Json,
            status = HttpStatusCode.OK,
            text = "user"
        )
    }
}