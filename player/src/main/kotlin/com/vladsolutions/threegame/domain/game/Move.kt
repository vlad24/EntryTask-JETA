package com.vladsolutions.threegame.domain.game

enum class Move(private val adjuster: Int) {

    INCREASE(1),
    KEEP(0),
    DECREASE(-1)
    ;

    fun apply(balance: Int): Int = (balance + adjuster)
}
