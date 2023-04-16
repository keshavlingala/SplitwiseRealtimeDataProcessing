package org.datbase.demo.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface SplitwiseApi {
	@GET("get_current_user")
	fun getCurrentUser(@Header("Authorization") authToken: String): Call<Any>

	@GET("get_expenses")
	fun getExpenses(@Header("Authorization") authToken: String): Call<Any>

	@GET("get_groups")
	fun getGroups(@Header("Authorization") authToken: String): Call<Any>
}