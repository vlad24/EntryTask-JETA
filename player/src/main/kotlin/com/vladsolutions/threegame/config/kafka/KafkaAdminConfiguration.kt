package com.vladsolutions.threegame.config.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.KafkaAdmin

@EnableKafka
@Configuration
class KafkaAdminConfiguration {


    @Autowired
    private lateinit var kafkaProperties: KafkaProperties

    @Bean
    fun kafkaAdmin(): KafkaAdmin = KafkaAdmin(kafkaProperties.buildAdminProperties())

    @Bean
    fun gameTopic(): NewTopic = NewTopic(
        KafkaTopics.GAME_TOPIC,
        3,
        1.toShort(),
    )



}
