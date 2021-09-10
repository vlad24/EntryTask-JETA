package com.vladsolutions.threegame.gameplay.input

import com.vladsolutions.threegame.domain.game.Move

class ConsoleGameInputParser : GameInputParser {

    override fun scanBalance(): Int {
        var balance: Int?
        do {
            println("> Please input balance (> 1):")
            balance = readLine().orEmpty().trim().lowercase()
                .toIntOrNull()
                ?.takeIf { it > 1 }
        } while (balance == null)
        return balance
    }

    override fun scanMoveId(): Move {
        println("> Please put: (-1 | 0 | 1)")
        var move: Move?
        do {
            val input = readLine().orEmpty().trim().lowercase().toIntOrNull()
            move = when (input) {
                1 -> Move.INCREASE
                0 -> Move.KEEP
                -1 -> Move.DECREASE
                else -> null
            }
        } while (move == null)
        return move
    }

    override fun scanYesNo(): Boolean {
        println("Please put: ( y | n )")
        var answer: Boolean?
        do {
            val input = readLine().orEmpty().trim().lowercase()
            answer = when (input) {
                "y" -> true
                "n" -> false
                else -> null
            }
        } while (answer == null)
        return answer
    }
}