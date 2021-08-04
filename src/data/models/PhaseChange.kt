package com.joshportfolio.data.models

import com.joshportfolio.data.Room
import com.joshportfolio.other.Constants.TYPE_PHASE_CHANGE

data class PhaseChange(
    var phase: Room.Phase?,
    var time: Long,
    val drawingPlayer: String? = null
) : BaseModel(TYPE_PHASE_CHANGE)
