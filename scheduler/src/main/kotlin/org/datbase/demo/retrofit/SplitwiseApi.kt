package org.datbase.demo.retrofit

import org.datbase.demo.dto.ExpenseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SplitwiseApi {
	@GET("get_current_user")
	fun getCurrentUser(@Header("Authorization") authToken: String): Call<Any>

	@GET("get_expenses")
	fun getExpenses(
		@Header(
			"Authorization",
		) authToken: String,
		@Query("limit") limit: Int = 20,
		@Query("offset") offset: Int = 0,
	): Call<ExpenseResponse>

	@GET("get_groups")
	fun getGroups(@Header("Authorization") authToken: String): Call<Any>
}
