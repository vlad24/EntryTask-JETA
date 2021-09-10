package com.vladsolutions.threegame.gameplay.input

import com.vladsolutions.threegame.domain.game.Move

interface GameInputParser {

    fun scanBalance(): Int

    fun scanMoveId(): Move

    fun scanYesNo(): Boolean

}
