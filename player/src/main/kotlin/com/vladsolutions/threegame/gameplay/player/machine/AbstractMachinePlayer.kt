package com.vladsolutions.threegame.gameplay.player.machine

import com.vladsolutions.threegame.domain.game.Move
import com.vladsolutions.threegame.domain.game.PlayerNumberEnum
import com.vladsolutions.threegame.gameplay.GameIdGenerator
import com.vladsolutions.threegame.gameplay.player.AbstractPlayer
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.random.Random

abstract class AbstractMachinePlayer(
    number: Number,
    idGenerator: GameIdGenerator,
    private val startBalanceFrom: Int?,
    private val startBalanceTo: Int?,
    protected val moveDelaySec: Int?
) : AbstractPlayer(number, idGenerator) {


    override fun decideOnInitialBalance(): Int {
        val first = Random.nextInt(
            from = startBalanceFrom ?: 10,
            until = startBalanceTo ?: 100
        )
        LOG.trace("Starting with {}" , first)
        return first
    }

    override fun decideOnNextMove(moveNum: Int, balance: Int): Move {
        LOG.trace("Sleeping {} seconds after move {} with {}", moveDelaySec, moveNum, balance)
        moveDelaySec
            ?.takeIf { it > 0 }
            ?.let {
                sleep(Duration.ofSeconds(it.toLong()).toMillis())
            }
        return computeNextMove(moveNum, balance)
    }

    override fun decideIfInitiateNewGame(): Boolean  {
        LOG.trace("First player, initiating a new game")
        return type == PlayerNumberEnum.FIRST
    }

    override fun decideIfRevengeNeeded(): Boolean = false

    protected abstract fun computeNextMove(moveNum: Int, balance: Int): Move

    companion object {
        private val LOG = LoggerFactory.getLogger(AbstractPlayer::class.java)
    }

}