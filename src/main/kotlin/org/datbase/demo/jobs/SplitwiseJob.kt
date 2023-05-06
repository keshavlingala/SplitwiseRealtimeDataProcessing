package org.datbase.demo.jobs

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.datbase.demo.multiAsync
import org.datbase.demo.services.CassandraService
import org.datbase.demo.services.MessagingService
import org.datbase.demo.services.SplitwiseService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.lang.Thread.sleep

@Service
class SplitwiseJob(
	private val cassandraService: CassandraService,
	private val messagingService: MessagingService,
	private val objectMapper: ObjectMapper,
	private val splitwiseService: SplitwiseService,
) {
	//	86400000 milliseconds = 24 hours = 1 day Runs every day at 00:00:00
	//	Runs in 10 seconds after the application starts
	@Scheduled(fixedRate = 86400000L, initialDelay = 10000)
	fun runEveryTwoMinutes() {
		val users = cassandraService.getAllUserApiKeys()
		users.multiAsync { user ->
			println("Getting expenses for user ${user.user_id}")
			val expenses = splitwiseService.getExpenses(user.api_key)
			expenses.forEach { obj ->
				//	For testing and debugging and demo purposes
//				sleep(1000)
				val expense = objectMapper.valueToTree<JsonNode>(obj)
				val expenseObj = expense as ObjectNode
				expenseObj.put("user_id", user.user_id)
				expenseObj.put("user_name", user.name)
//				println("Sending expense to kafka: ${expense.get("id")}")
				messagingService.sendExpenseToKafka(expense)
			}
			println("Done getting expenses for user ${user.name}")
		}
	}
}