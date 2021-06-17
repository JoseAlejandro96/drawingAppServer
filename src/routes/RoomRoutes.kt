package com.joshportfolio.routes

import com.joshportfolio.data.Room
import com.joshportfolio.data.models.BasicApiResponse
import com.joshportfolio.data.models.CreateRoomRequest
import com.joshportfolio.data.models.RoomResponse
import com.joshportfolio.other.Constants.MAX_ROOM_SIZE
import com.joshportfolio.server
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createRoomRoute(){
    route("/api/createRoom"){
        post {
            val roomRequest = call.receiveOrNull<CreateRoomRequest>()
            if (roomRequest === null){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if(server.rooms[roomRequest.name] != null){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "Room already exists.")
                )
                return@post
            }
            if(roomRequest.maxPlayers < 2){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "The minimum room size is 2.")
                )
                return@post
            }
            if(roomRequest.maxPlayers > MAX_ROOM_SIZE){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "The maximum room size is $MAX_ROOM_SIZE.")
                )
                return@post
            }

            val room = Room(
                roomRequest.name,
                roomRequest.maxPlayers
            )
            server.rooms[roomRequest.name] = room
            println("Room created: ${roomRequest.name}")

            call.respond(HttpStatusCode.OK, BasicApiResponse(true))
        }
    }
}

fun Route.getRoomsRoute(){
    route("/api/getRooms"){
        get {
            val searchQuery = call.parameters["searchQuery"]
            if(searchQuery == null){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val roomsResult = server.rooms.filterKeys {
                it.contains(searchQuery, ignoreCase = true)
            }
            val roomResponses = roomsResult.values.map {
                RoomResponse(it.name, it.maxPlayers, it.players.size)
            }.sortedBy { it.name }

            call.respond(HttpStatusCode.OK,roomResponses)
        }
    }
}