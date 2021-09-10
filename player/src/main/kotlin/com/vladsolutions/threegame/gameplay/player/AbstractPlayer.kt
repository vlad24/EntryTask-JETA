package com.vladsolutions.threegame.gameplay.player

import com.vladsolutions.threegame.domain.game.PlayerNumberEnum
import com.vladsolutions.threegame.domain.event.*
import com.vladsolutions.threegame.domain.game.Move
import com.vladsolutions.threegame.gameplay.GameIdGenerator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
abstract class AbstractPlayer @Autowired constructor(
    playerNumber: Number,
    private val gameIdGenerator: GameIdGenerator
): Player {

    protected final val type: PlayerNumberEnum = PlayerNumberEnum.from(playerNumber)
        ?: throw IllegalArgumentException("Player number can be either 1 or 2")

    private val id = type.number.toLong()

    override fun id(): Long = id

    override fun onLaunch(): GameEvent? {
        return if (decideIfInitiateNewGame()){
            val gameId = gameIdGenerator.nextId()
            val initialBalance = decideOnInitialBalance()
            LOG.info("Player {} starts game {} with initial balance: {}", id, gameId, initialBalance)
            GameStart(gameId = gameId, emitterId = id, balance = initialBalance)
        } else {
            null
        }
    }

    override fun onGameStart(event: GameStart): GameEvent? {
        LOG.info("Game started: {}. Let's make first move!", event.gameId)
        return makeNextMove(event.gameId, 1, event.balance)
    }

    override fun onGameMove(event: GameMove): GameEvent? {
        return makeNextMove(event.gameId, event.moveSeq + 1, event.balance)
    }

    override fun onGameEnd(event: GameEnd) : GameEvent? {
        LOG.info("Game {} over. Winner: player {}", event.gameId, event.winnerId)
        return if (decideIfRevengeNeeded()) {
            LOG.info("Player {} wants a revenge match!", id)
            GameStart(
                gameId = gameIdGenerator.nextId(),
                emitterId = id,
                balance = decideOnInitialBalance()
            )
        } else {
            LOG.info("Player {} thinks it is enough for today", id)
            null
        }
    }

    private fun makeNextMove(gameId: Long, moveNum: Int, balance: Int): GameEvent {
        val move = decideOnNextMove(moveNum, balance)
        var newBalance = move.apply(balance)
        if (newBalance % 3 == 0){
            newBalance /= 3
        }
        LOG.info("Player {} made move N{} in game '{}' | {} {} resulting in {}", id, moveNum, gameId, move, balance, newBalance)
        return if (newBalance == 0 || newBalance == 1) {
            LOG.info("Player {} WON the game {} !!!", id, gameId)
            GameEnd(gameId = gameId, emitterId = id, winnerId = id)
        } else {
            GameMove(gameId = gameId, emitterId = id, balance = newBalance, gottenBy = move, moveSeq = moveNum)
        }
    }

    protected abstract fun decideIfInitiateNewGame(): Boolean

    protected abstract fun decideOnInitialBalance(): Int

    protected abstract fun decideOnNextMove(moveNum: Int, balance: Int): Move

    protected abstract fun decideIfRevengeNeeded(): Boolean

    companion object {
        private val LOG = LoggerFactory.getLogger(AbstractPlayer::class.java)
    }

}