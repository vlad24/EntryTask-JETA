package com.vladsolutions.threegame.gameplay.player.machine

import com.vladsolutions.threegame.domain.game.Move
import com.vladsolutions.threegame.gameplay.player.AbstractPlayer
import com.vladsolutions.threegame.gameplay.GameIdGenerator
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.time.Duration

class DivisionDrivenMachinePlayer(
    number: Number,
    idGenerator: GameIdGenerator,
    startBalanceFrom: Int? = 10,
    startBalanceTo: Int? = 100,
    moveDelaySec: Int? = 1
) : AbstractMachinePlayer(number, idGenerator, startBalanceFrom, startBalanceTo, moveDelaySec) {

    override fun computeNextMove(moveNum: Int, balance: Int): Move {
        LOG.debug("Thinking how to handle {} move with {}", moveNum, balance)
        moveDelaySec?.takeIf { it > 0 }?.let { sleep(Duration.ofSeconds(it.toLong()).toMillis()) }
        return Move.values().first { it.apply(balance) % 3 == 0 }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AbstractPlayer::class.java)
    }

}