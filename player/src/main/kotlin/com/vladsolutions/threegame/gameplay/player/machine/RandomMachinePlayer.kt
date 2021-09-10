package com.vladsolutions.threegame.gameplay.player.machine

import com.vladsolutions.threegame.domain.game.Move
import com.vladsolutions.threegame.gameplay.player.AbstractPlayer
import com.vladsolutions.threegame.gameplay.GameIdGenerator
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.random.Random

class RandomMachinePlayer(
    number: Byte,
    idGenerator: GameIdGenerator,
    startBalanceFrom: Int?,
    startBalanceTo: Int?,
    moveDelaySec: Int?
) : AbstractMachinePlayer(number, idGenerator, startBalanceFrom, startBalanceTo, moveDelaySec) {


    override fun computeNextMove(moveNum: Int, balance: Int): Move {
        val next = abs((Random.nextInt() % Move.values().size))
        return Move.values()[next]
    }

    companion object {
        private val LOG =  LoggerFactory.getLogger(AbstractPlayer::class.java)
    }
}
