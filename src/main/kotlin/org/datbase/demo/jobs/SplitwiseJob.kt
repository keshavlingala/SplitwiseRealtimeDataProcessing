package org.datbase.demo.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import org.datbase.demo.asJWTToken
import org.datbase.demo.retrofit.SplitwiseApi
import org.datbase.demo.services.CassandraService
import org.datbase.demo.services.MessagingService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SplitwiseJob(
	private val cassandraService: CassandraService,
	private val messagingService: MessagingService,
	private val splitwiseApi: SplitwiseApi,
	private val objectMapper: ObjectMapper,
) {
//	@Scheduled(fixedRate = 120000)
	fun runEveryTwoMinutes() {
		val users = cassandraService.getAllUserApiKeys()
		users.forEach { user ->
			println("Getting expenses for user ${user.user_id}")
			val response = splitwiseApi.getExpenses(user.api_key.asJWTToken()).execute()
			if (response.isSuccessful) {
				val json = objectMapper.writeValueAsString(response.body())
				val expenses = objectMapper.readTree(json).get("expenses")
				expenses.forEach { expense ->
					val isGroupExpense = expense.get("group_id")
					println("Type of groupExpense is ${isGroupExpense?.javaClass} and value is $isGroupExpense")
					if (isGroupExpense.isNull.not()) {
						println("Group Expense: ${expense.get("description").asText()}")
//						messagingService.sendGroupExpenses(expense)
					} else {
						println("Individual Expense: ${expense.get("description").asText()}")
//						messagingService.sendIndividual(expense)
					}
				}
			} else {
				println("Error getting expenses for user ${user.user_id} with error ${response.errorBody()} auth Code :${user.api_key}")
			}
		}
	}
}