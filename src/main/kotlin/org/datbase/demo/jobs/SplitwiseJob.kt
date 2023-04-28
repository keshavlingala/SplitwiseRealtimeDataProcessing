package org.datbase.demo.jobs

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.datbase.demo.services.CassandraService
import org.datbase.demo.services.MessagingService
import org.datbase.demo.services.SplitwiseService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SplitwiseJob(
	private val cassandraService: CassandraService,
	private val messagingService: MessagingService,
	private val objectMapper: ObjectMapper,
	private val splitwiseService: SplitwiseService,
) {
	@Scheduled(fixedRate = 86400000L, initialDelay = 10000)
	fun runEveryTwoMinutes() {
		val users = cassandraService.getAllUserApiKeys()
		users.forEach { user ->
			println("Getting expenses for user ${user.user_id}")
			val expenses = splitwiseService.getExpenses(user.api_key)
			expenses.forEach { obj ->
//				sleep(1000)
				val expense = objectMapper.valueToTree<JsonNode>(obj)
				val isGroupExpense = expense.get("group_id")
				if (isGroupExpense.isNull.not()) {
					messagingService.sendGroupExpenses(expense)
				} else {
					messagingService.sendIndividual(expense)
				}
			}
		}
	}
}