package com.joshportfolio

import com.google.gson.Gson
import com.joshportfolio.routes.createRoomRoute
import com.joshportfolio.routes.getRoomsRoute
import com.joshportfolio.routes.joinRoomRoute
import com.joshportfolio.session.DrawingSession
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.sessions.*
import io.ktor.util.*
import java.time.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val server = DrawingServer()
val gson = Gson()

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Sessions) {
        cookie<DrawingSession>("SESSION")
    }
    /*
    * Intercepta las conexiones de los clientes
    * y comprueba si el cliente ya tiene una session, si no la tiene le asigna una nueva session*/
    intercept(ApplicationCallPipeline.Features){
        if(call.sessions.get<DrawingSession>() == null){
            val clientId = call.parameters["client_id"] ?: ""
            call.sessions.set(DrawingSession(clientId, generateNonce()))
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(CallLogging)

    install(WebSockets)

    install(Routing){
        createRoomRoute()
        getRoomsRoute()
        joinRoomRoute()
    }
}







/*
    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }

        webSocket("/myws/echo") {
            send(Frame.Text("Hi from server"))
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    send(Frame.Text("Client said: " + frame.readText()))
                }
            }
        }
    }
    */

