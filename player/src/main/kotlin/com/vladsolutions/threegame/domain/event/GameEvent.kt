package com.vladsolutions.threegame.domain.event

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.vladsolutions.threegame.domain.game.Move
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes(
    JsonSubTypes.Type(value = GameMove::class)
)
sealed class GameEvent(
    val eventId: String,
    val gameId: Long,
    val emitterId: Long,
)


@JsonTypeName("GameMove")
class GameMove(
    eventId: String = UUID.randomUUID().toString(),
    gameId: Long,
    emitterId: Long,
    val moveSeq: Int,
    val balance: Int,
    val gottenBy: Move,
) : GameEvent(eventId, gameId, emitterId)


@JsonTypeName("GameStart")
class GameStart(
    eventId: String = UUID.randomUUID().toString(),
    gameId: Long,
    emitterId: Long,
    val balance: Int,
) : GameEvent(eventId, gameId, emitterId)


@JsonTypeName("GameEnd")
class GameEnd(
    eventId: String = UUID.randomUUID().toString(),
    gameId: Long,
    emitterId: Long,
    val winnerId: Long,
) : GameEvent(
    eventId = eventId,
    gameId = gameId,
    emitterId = emitterId
)