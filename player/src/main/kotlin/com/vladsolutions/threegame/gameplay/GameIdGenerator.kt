package com.vladsolutions.threegame.gameplay

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class GameIdGenerator @Autowired constructor(private val clock: Clock) {
    fun nextId(): Long{
        return clock.millis()
    }
}