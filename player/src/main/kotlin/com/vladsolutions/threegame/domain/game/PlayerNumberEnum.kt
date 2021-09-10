package com.vladsolutions.threegame.domain.game

enum class PlayerNumberEnum(val number: Int) {
    FIRST(1),
    SECOND(2),
    ;

    companion object {
        @JvmStatic
        fun from(typeId: Number): PlayerNumberEnum? = values().firstOrNull { it.number == typeId.toInt() }
    }

}
