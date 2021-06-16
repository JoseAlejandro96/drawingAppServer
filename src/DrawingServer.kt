package com.joshportfolio

import com.joshportfolio.data.Player
import com.joshportfolio.data.Room
import java.util.concurrent.ConcurrentHashMap

class DrawingServer {
    //el String es el nombre de la sala y regresa toda la info de esa sala
    val rooms = ConcurrentHashMap<String, Room>()
    val players = ConcurrentHashMap<String, Player>()

    fun playerJoined(player: Player){
        players[player.clientId] = player
    }

    // Devuelve la primera sala donde encuentra el clientId
    fun getRoomWithClientId(clientId: String) : Room?{
        val filteredRooms = rooms.filterValues { room ->
            room.players.find { player ->
                player.clientId == clientId
            } != null
        }
        return if(filteredRooms.values.isEmpty()){
            null
        }else{
            return filteredRooms.values.toList()[0]
        }
    }
}