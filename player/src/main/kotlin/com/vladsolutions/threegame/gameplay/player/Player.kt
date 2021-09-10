package com.vladsolutions.threegame.gameplay.player

import com.vladsolutions.threegame.domain.event.GameStart
import com.vladsolutions.threegame.domain.event.GameEnd
import com.vladsolutions.threegame.domain.event.GameEvent
import com.vladsolutions.threegame.domain.event.GameMove

interface Player {
    fun id(): Long
    fun onLaunch(): GameEvent?
    fun onGameStart(event: GameStart): GameEvent?
    fun onGameMove(event: GameMove): GameEvent?
    fun onGameEnd(event: GameEnd): GameEvent?
}