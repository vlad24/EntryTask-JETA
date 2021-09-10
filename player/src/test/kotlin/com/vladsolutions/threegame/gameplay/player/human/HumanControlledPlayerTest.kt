package com.vladsolutions.threegame.gameplay.player.human

import com.vladsolutions.threegame.domain.event.GameEnd
import com.vladsolutions.threegame.domain.event.GameMove
import com.vladsolutions.threegame.domain.event.GameStart
import com.vladsolutions.threegame.domain.game.Move
import com.vladsolutions.threegame.domain.game.PlayerNumberEnum
import com.vladsolutions.threegame.gameplay.GameIdGenerator
import com.vladsolutions.threegame.gameplay.input.ConsoleGameInputParser
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class HumanControlledPlayerTest {

    private lateinit var player: HumanControlledPlayer

    @MockK
    lateinit var idGenerator: GameIdGenerator

    @MockK
    lateinit var gameInputParser: ConsoleGameInputParser

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { idGenerator.nextId() } returns 2
        player = HumanControlledPlayer(number = PlayerNumberEnum.FIRST.number, idGenerator = idGenerator, gameInputParser = gameInputParser)
    }


    @AfterEach
    fun tearDown() {
        clearMocks(idGenerator, gameInputParser)
    }


    @Test
    fun `when starts -- then asked if starts game`() {
        every { gameInputParser.scanYesNo() } returns true
        every { gameInputParser.scanBalance() } returns 7
        player.onLaunch()
        verify {
            gameInputParser.scanYesNo()
        }
    }

    @Test
    fun `when starts and wants to start -- then game started`() {
        every { gameInputParser.scanYesNo() } returns true
        every { gameInputParser.scanBalance() } returns 7
        assertNotNull(player.onLaunch())
        verify (exactly = 1) {
            gameInputParser.scanBalance()
        }
    }

    @Test
    fun `when starts and refuses to start -- then game not started`() {
        every { gameInputParser.scanYesNo() } returns false
        every { gameInputParser.scanBalance() } returns 7
        assertNull(player.onLaunch())
        verify (exactly = 0) {
            gameInputParser.scanBalance()
        }
    }


    @ParameterizedTest
    @ValueSource(ints = [5, 11, 332])
    fun `when human increases from 3+3n-1 -- then increased and divided by 3`(balance: Int) {
        every { gameInputParser.scanMoveId() } returns Move.INCREASE
        val move = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(move)
        move as GameMove
        assertEquals(Move.INCREASE, move.gottenBy)
        assertEquals((balance + 1) / 3, move.balance)
    }

    @ParameterizedTest
    @ValueSource(ints = [7, 13, 334])
    fun `when human decreases from 3+3n+1 -- then decreased and divided by 3`(balance: Int) {
        every { gameInputParser.scanMoveId() } returns Move.DECREASE
        val move = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(move)
        move as GameMove
        assertEquals(Move.DECREASE, move.gottenBy)
        assertEquals(balance / 3, move.balance)
    }

    @ParameterizedTest
    @ValueSource(ints = [9, 12, 336])
    fun `when human keeps balance==3+3n -- then divided by 3`(balance: Int) {
        every { gameInputParser.scanMoveId() } returns Move.KEEP
        val move = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(move)
        move as GameMove
        assertEquals(Move.KEEP, move.gottenBy)
        assertEquals(balance / 3, move.balance)
    }


    @ParameterizedTest
    @ValueSource(ints = [9, 12, 19, 91, 336])
    fun `when human increases balance to not divisible by 3 -- then just increased`(balance: Int) {
        every { gameInputParser.scanMoveId() } returns Move.INCREASE
        val move = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(move)
        move as GameMove
        assertEquals(Move.INCREASE, move.gottenBy)
        assertEquals(balance + 1, move.balance)
    }

    @ParameterizedTest
    @ValueSource(ints = [9, 12, 17, 89, 332])
    fun `when human decreases balance to not divisible by 3 -- then just decreased`(balance: Int) {
        every { gameInputParser.scanMoveId() } returns Move.DECREASE
        val move = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(move)
        move as GameMove
        assertEquals(Move.DECREASE, move.gottenBy)
        assertEquals(balance - 1, move.balance)
    }

    @ParameterizedTest
    @ValueSource(ints = [8, 11, 17, 89, 332])
    fun `when human keeps balance not divisible by 3 -- then just untouched`(balance: Int) {
        every { gameInputParser.scanMoveId() } returns Move.DECREASE
        val move = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(move)
        move as GameMove
        assertEquals(Move.DECREASE, move.gottenBy)
        assertEquals(balance - 1, move.balance)
    }

    @Test
    fun `when moving with 3 -- then ends game`() {
        every { gameInputParser.scanMoveId() } returns Move.KEEP
        val move = player.onGameMove(getMoveWithBalance(3))
        assertNotNull(move)
        assertTrue(move is GameEnd)
        move as GameEnd
        assertEquals(player.id(), move.winnerId)
    }

    @Test
    fun `when moving with 0 -- then ends game`() {
        every { gameInputParser.scanMoveId() } returns Move.KEEP
        val move = player.onGameMove(getMoveWithBalance(0))
        assertNotNull(move)
        assertTrue(move is GameEnd)
        move as GameEnd
        assertEquals(player.id(), move.winnerId)
    }

    @Test
    fun `when game ends and user wants to play more -- then new game started game`() {
        every { gameInputParser.scanYesNo() } returns true
        every { gameInputParser.scanBalance() } returns 7
        val move = player.onGameEnd(GameEnd(gameId = 1, emitterId = 2, winnerId = 2))
        assertNotNull(move)
        assertTrue(move is GameStart)
        assertEquals(7, (move as GameStart).balance)
    }

    private fun getMoveWithBalance(balance: Int) = GameMove(
        gameId = 1,
        emitterId = 1,
        moveSeq = 1,
        balance = balance,
        gottenBy = Move.KEEP
    )

}