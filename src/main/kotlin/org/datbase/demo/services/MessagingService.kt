package org.datbase.demo.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MessagingService(
	val kafkaTemplate: KafkaTemplate<String, String>,
	val objectMapper: ObjectMapper,
) {
	fun sendIndividual(obj: Any) {
		kafkaTemplate.send("individual-expenses", objectMapper.writeValueAsString(obj))
	}

	fun sendGroupExpenses(obj: Any) {
		kafkaTemplate.send("group-expenses", objectMapper.writeValueAsString(obj))
	}

	fun sendMonthlyExpenses(obj: Any) {
		kafkaTemplate.send("monthly-expenses", objectMapper.writeValueAsString(obj))
	}

}