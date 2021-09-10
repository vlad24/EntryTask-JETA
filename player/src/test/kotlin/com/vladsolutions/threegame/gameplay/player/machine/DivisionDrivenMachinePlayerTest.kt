package com.vladsolutions.threegame.gameplay.player.machine

import com.vladsolutions.threegame.domain.event.GameEnd
import com.vladsolutions.threegame.domain.event.GameMove
import com.vladsolutions.threegame.domain.event.GameStart
import com.vladsolutions.threegame.domain.game.Move
import com.vladsolutions.threegame.domain.game.PlayerNumberEnum.FIRST
import com.vladsolutions.threegame.domain.game.PlayerNumberEnum.SECOND
import com.vladsolutions.threegame.gameplay.GameIdGenerator
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class DivisionDrivenMachinePlayerTest {

    private lateinit var player: DivisionDrivenMachinePlayer

    @MockK
    lateinit var idGenerator: GameIdGenerator

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { idGenerator.nextId() } returns 2
        player = DivisionDrivenMachinePlayer(number = FIRST.number, idGenerator = idGenerator, moveDelaySec = 0)
    }


    @AfterEach
    fun tearDown() {
        clearMocks(idGenerator)
    }


    @ParameterizedTest
    @ValueSource(ints = [6, 9, 333])
    fun `when moving with balance==3+3n -- then just divided by 3`(balance: Int) {
        val next = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(next)
        next as GameMove
        assertEquals(Move.KEEP, next.gottenBy)
        assertEquals(balance / 3, next.balance)
    }

    @ParameterizedTest
    @ValueSource(ints = [7, 10, 334])
    fun `when moving with balance==3+3n+1 -- then decreased and divided by 3`(balance: Int) {
        val next = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(next)
        assertTrue(next is GameMove)
        next as GameMove
        assertEquals(Move.DECREASE, next.gottenBy)
        assertEquals((balance - 1) / 3, next.balance)
    }

    @ParameterizedTest
    @ValueSource(ints = [5, 8, 332])
    fun `when moving with balance==3+3n-1 -- then increased and divided by 3`(balance: Int) {
        val next = player.onGameMove(getMoveWithBalance(balance))
        assertNotNull(next)
        assertTrue(next is GameMove)
        next as GameMove
        assertEquals(Move.INCREASE, next.gottenBy)
        assertEquals((balance + 1) / 3, next.balance)
    }

    @Test
    fun `when moving with 3 -- then game won`() {
        val next = player.onGameMove(getMoveWithBalance(3))
        assertNotNull(next)
        assertTrue(next is GameEnd)
        next as GameEnd
        assertEquals(player.id(), next.winnerId)
    }


    @Test
    fun `when moving with 2 -- then game won`() {
        val next = player.onGameMove(getMoveWithBalance(2))
        assertNotNull(next)
        assertTrue(next is GameEnd)
        next as GameEnd
        assertEquals(player.id(), next.winnerId)
    }


    @Test
    fun `when moving with 4 -- then game won`() {
        val next = player.onGameMove(getMoveWithBalance(3))
        assertNotNull(next)
        assertTrue(next is GameEnd)
        next as GameEnd
        assertEquals(player.id(), next.winnerId)
    }

    @Test
    fun `when moving with 0 -- then game won`() {
        val next = player.onGameMove(getMoveWithBalance(0))
        assertNotNull(next)
        assertTrue(next is GameEnd)
        next as GameEnd
        assertEquals(player.id(), next.winnerId)
    }

    @Test
    fun `when game ends -- then does not start another game`() {
        val next = player.onGameEnd(GameEnd(gameId = 1, emitterId = 2, winnerId = 2))
        assertNull(next)
    }


    @Test
    fun `when zero balance -- then immediate victory`() {
        val next = player.onGameMove(GameMove(gameId = 1, emitterId = 2, moveSeq = 6, balance = 0, gottenBy = Move.KEEP))
        assertNotNull(next)
        assertTrue(next is GameEnd)
        assertEquals(1, (next as GameEnd).winnerId)
    }


    @Test
    fun `when is first -- then starts game`() {
        player = DivisionDrivenMachinePlayer(number = FIRST.number, idGenerator = idGenerator, moveDelaySec = 0)
        val next = player.onLaunch()
        assertNotNull(next)
        assertTrue(next is GameStart)
    }

    @Test
    fun `when is second -- then does not start game`() {
        player = DivisionDrivenMachinePlayer(number = SECOND.number, idGenerator = idGenerator, moveDelaySec = 0)
        val next = player.onLaunch()
        assertNull(next)
    }

    private fun getMoveWithBalance(balance: Int) = GameMove(
        gameId = 1,
        emitterId = 1,
        moveSeq = 1,
        balance = balance,
        gottenBy = Move.KEEP
    )
}