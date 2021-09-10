package com.vladsolutions.threegame.gameplay.player.human

import com.vladsolutions.threegame.domain.game.Move
import com.vladsolutions.threegame.gameplay.player.AbstractPlayer
import com.vladsolutions.threegame.gameplay.GameIdGenerator
import com.vladsolutions.threegame.gameplay.input.GameInputParser
import org.slf4j.LoggerFactory

class HumanControlledPlayer(
    number: Number,
    idGenerator: GameIdGenerator,
    private val gameInputParser: GameInputParser
) : AbstractPlayer(number, idGenerator) {

    override fun decideOnInitialBalance(): Int = gameInputParser.scanBalance()

    override fun decideOnNextMove(moveNum: Int, balance: Int): Move {
        println("### Move $moveNum: balance = $balance")
        return gameInputParser.scanMoveId()
    }

    override fun decideIfInitiateNewGame(): Boolean {
        println("### Shall we start new game?")
        return gameInputParser.scanYesNo()
    }

    override fun decideIfRevengeNeeded(): Boolean {
        println("### Want some more?")
        return gameInputParser.scanYesNo()
    }


    companion object {
        private val LOG =  LoggerFactory.getLogger(AbstractPlayer::class.java)
    }

}