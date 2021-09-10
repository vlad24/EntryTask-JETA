package com.vladsolutions.threegame.kafka

import com.vladsolutions.threegame.config.kafka.KafkaTopics
import com.vladsolutions.threegame.gameplay.player.Player
import com.vladsolutions.threegame.domain.event.GameEnd
import com.vladsolutions.threegame.domain.event.GameEvent
import com.vladsolutions.threegame.domain.event.GameMove
import com.vladsolutions.threegame.domain.event.GameStart
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Component
class KafkaGameEventHandler @Autowired constructor(
    private val kafkaTemplate: KafkaTemplate<String, GameEvent?>,
    private val player: Player
) {

    @PostConstruct
    fun init() {
        LOG.info("Setting up kafka for player {}", player.id())
        kafkaTemplate.executeInTransaction {
            player.onLaunch()?.also { event ->
                it.send(KafkaTopics.GAME_TOPIC, event.gameId.toString(), event)
                    .get(1, TimeUnit.MINUTES)
            }
        }
    }

    @KafkaListener(topics = [KafkaTopics.GAME_TOPIC], autoStartup = "true")
    fun processGameEvent(@Payload event: GameEvent) {
        if (event.emitterId == player.id()) return
        LOG.trace("Received event : {}", event.eventId)
        LOG.info("Player {}: got message from player {} for game {}", player.id(), event.emitterId, event.gameId, event.eventId)
        when (event) {
            is GameStart -> handleAndReply(event, player::onGameStart)
            is GameMove -> handleAndReply(event, player::onGameMove)
            is GameEnd -> handleAndReply(event, player::onGameEnd)
        }
    }

    private fun <T : GameEvent> handleAndReply(event: T, handlerFunc: (T) -> GameEvent?) {
        kafkaTemplate.executeInTransaction {
            handlerFunc(event)?.also { response ->
                val result = kafkaTemplate
                    .send(KafkaTopics.GAME_TOPIC, event.gameId.toString(), response)
                    .get(1, TimeUnit.MINUTES)
                LOG.trace("Sent event {}. Metadata: {}", event, result)
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KafkaGameEventHandler::class.java)
    }

}