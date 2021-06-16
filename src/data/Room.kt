package com.joshportfolio.data

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.isActive

class Room(
    val name: String,
    val maxPlayers: Int,
    var players: List<Player> = listOf()
) {

    // Envia un mensaje a cada jugador de la sala que tenga wl socket activo o valido.
    suspend fun broadcast(message: String){
        players.forEach{ player ->  
            if(player.socket.isActive){
                player.socket.send(Frame.Text(message))
            }
        }
    }

    suspend fun broadcastToAllExcept(message: String, clientId: String){
        players.forEach{ player ->
            if(player.clientId != clientId && player.socket.isActive){
                player.socket.send(Frame.Text(message))
            }
        }
    }

}