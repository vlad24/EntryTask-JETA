package com.vladsolutions.threegame.config.app

import com.vladsolutions.threegame.gameplay.GameIdGenerator
import com.vladsolutions.threegame.gameplay.player.machine.DivisionDrivenMachinePlayer
import com.vladsolutions.threegame.gameplay.player.human.HumanControlledPlayer
import com.vladsolutions.threegame.gameplay.player.machine.MoveTraversingMachinePlayer
import com.vladsolutions.threegame.gameplay.player.machine.RandomMachinePlayer
import com.vladsolutions.threegame.gameplay.input.ConsoleGameInputParser
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.validation.annotation.Validated
import java.time.Clock
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Configuration
@EnableConfigurationProperties(PlayerConfig::class)
class AppConfiguration {

    @Bean
    @ConditionalOnProperty(name = ["player.mode"], havingValue = "AUTO_OPTIMAL", matchIfMissing = true)
    fun optimalMachinePlayer(playerConfig: PlayerConfig, idGenerator: GameIdGenerator) = DivisionDrivenMachinePlayer(
        number = playerConfig.type,
        idGenerator = idGenerator,
        startBalanceFrom = playerConfig.startBalanceFrom,
        startBalanceTo = playerConfig.startBalanceTo,
        moveDelaySec = playerConfig.moveDelaySec,
    )

    @Bean
    @ConditionalOnProperty(name = ["player.mode"], havingValue = "AUTO_TRAVERSE", matchIfMissing = false)
    fun seqMachinePlayer(config: PlayerConfig, idGenerator: GameIdGenerator) = MoveTraversingMachinePlayer(
        number = config.type,
        idGenerator = idGenerator,
        startBalanceFrom = config.startBalanceFrom,
        startBalanceTo = config.startBalanceTo,
        moveDelaySec = config.moveDelaySec,
    )

    @Bean
    @ConditionalOnProperty(name = ["player.mode"], havingValue = "AUTO_RANDOM", matchIfMissing = false)
    fun randomBehavingMachinePlayer(config: PlayerConfig, idGenerator: GameIdGenerator) = RandomMachinePlayer(
        number = config.type,
        idGenerator = idGenerator,
        startBalanceFrom = config.startBalanceFrom,
        startBalanceTo = config.startBalanceTo,
        moveDelaySec = config.moveDelaySec,
    )

    @Bean
    @ConditionalOnProperty(name = ["player.mode"], havingValue = "MANUAL", matchIfMissing = false)
    fun humanControlledPlayer(config: PlayerConfig, idGenerator: GameIdGenerator, gameInputParser: ConsoleGameInputParser) = HumanControlledPlayer(
        number = config.type,
        idGenerator = idGenerator,
        gameInputParser = gameInputParser
    )


    @Lazy
    @Bean
    fun gameInputParser(): ConsoleGameInputParser = ConsoleGameInputParser()


    @Lazy
    @Bean
    fun clock(): Clock = Clock.systemUTC()
}

@ConfigurationProperties(prefix = "player")
@Validated
data class PlayerConfig(
    @field:Min(1)
    @field:Max(2)
    var type: Byte = 1,

    @field:Min(4)
    var startBalanceFrom: Int = 50_000,

    @field:Min(4)
    var startBalanceTo: Int = 100_000,

    @field:Min(0)
    var moveDelaySec: Int = 1
)
