package org.datbase.demo.services

import org.datbase.demo.asJWTToken
import org.datbase.demo.retrofit.SplitwiseApi
import org.springframework.stereotype.Service

@Service
class SplitwiseService(
	private val splitwiseApi: SplitwiseApi,
) {
	// Creating a custom iterator to get all the expenses using the Splitwise API
	fun getExpenses(apiKey: String): Iterator<Any> {
		return object : Iterator<Any> {
			val limit = 20
			var offset = 0
			var expenses = listOf<Any>()
			var hasNext = true
			override fun hasNext(): Boolean {
				if (expenses.isEmpty() && hasNext) {
					val response = splitwiseApi.getExpenses(apiKey.asJWTToken(), limit, offset).execute()
					if (response.isSuccessful) {
						expenses = response.body()?.expenses ?: listOf()
						offset += limit
						hasNext = expenses.isNotEmpty()
					} else {
						hasNext = false
					}
				}
				return expenses.isNotEmpty()
			}

			override fun next(): Any {
				val expense = expenses.first()
				expenses = expenses.drop(1)
				return expense
			}
		}
	}
}