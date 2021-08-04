package com.joshportfolio.data

import com.joshportfolio.data.models.Announcement
import com.joshportfolio.data.models.PhaseChange
import com.joshportfolio.gson
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Room(
    val name: String,
    val maxPlayers: Int,
    var players: List<Player> = listOf()
) {

    private var timerJob: Job? = null
    private var drawingPlayer: Player? = null

    private var phaseChangedListener: ((Phase) -> Unit)? = null
    var phase = Phase.WAITING_FOR_PLAYERS
        set(value){
            synchronized(field){
                field = value
                phaseChangedListener?.let { change ->
                    change(value)
                }
            }
        }

    private fun setPhaseChangeListener(listener: (Phase) -> Unit){
        phaseChangedListener = listener
    }

    init {
        setPhaseChangeListener {    newPhase ->
            when(newPhase){
                Phase.WAITING_FOR_PLAYERS -> waitingForPlayers()
                Phase.WAITING_FOR_START -> waitingForStart()
                Phase.NEW_ROUND -> newRound()
                Phase.GAME_RUNNING -> gameRunning()
                Phase.SHOW_WORD -> showWord()
            }
        }
    }

    suspend fun addPlayer(clientId: String, username: String, socket: WebSocketSession): Player{
        val player = Player(username, socket, clientId)
        players = players + player

        if(players.size == 1){
            phase = Phase.WAITING_FOR_PLAYERS
        } else if(players.size == 2 && phase == Phase.WAITING_FOR_PLAYERS){
            phase = Phase.WAITING_FOR_START
            players = players.shuffled()
        } else if(phase == Phase.WAITING_FOR_START && players.size == maxPlayers){
            phase = Phase.NEW_ROUND
            players = players.shuffled()
        }

        val announcement = Announcement(
            "$username joined the party!",
            System.currentTimeMillis(),
            Announcement.TYPE_PLAYER_JOIN
        )

        broadcast(gson.toJson(announcement))

        return player
    }

    private fun timeAndNotify(ms: Long){
        timerJob?.cancel()
        timerJob = GlobalScope.launch {
            val phaseChange = PhaseChange(
                phase,
                ms,
                drawingPlayer?.userName
            )

        }
    }

    suspend fun broadcast(message: String){
        players.forEach { player ->
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

    fun containsPlayer(username: String): Boolean{
        return players.find { it.userName == username } != null
    }


    private fun waitingForPlayers(){

    }

    private fun waitingForStart(){

    }

    private fun newRound(){

    }

    private fun gameRunning(){

    }

    private fun showWord(){

    }

    enum class Phase {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_START,
        NEW_ROUND,
        GAME_RUNNING,
        SHOW_WORD
    }

    companion object{
        const val UPDATE_TIME_FREQUENCY = 1000L
    }

}