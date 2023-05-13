package org.datbase.demo.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.datbase.demo.asJWTToken
import org.datbase.demo.dto.UserApiKey
import org.datbase.demo.jobs.SplitwiseJob
import org.datbase.demo.retrofit.SplitwiseApi
import org.datbase.demo.services.CassandraService
import org.datbase.demo.services.MessagingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import retrofit2.http.Query
import java.time.LocalDate

@RestController
@RequestMapping("/")
class AddUserController(
	val splitwiseApi: SplitwiseApi,
	val cassandraService: CassandraService,
	val messagingService: MessagingService,
	val objectMapper: ObjectMapper
) {
	@ExceptionHandler
	fun handleException(e: Exception): ResponseEntity<Any> {
		return ResponseEntity.badRequest().body(e.message)
	}

	@GetMapping
	fun index(): ResponseEntity<Any> {
		return ResponseEntity.ok().body("Working!")
	}

	@PostMapping
	fun postObject(
		@RequestBody body: Any
	): ResponseEntity<Any> {
		messagingService.sendObj(body)
		return ResponseEntity.ok().body("Working!")
	}

	//	Take user's Name and apiKey from JSON for a POST Request
	@PostMapping("add_user_key")
	fun addUserApiKey(
		@Query("key") key: String,
	): ResponseEntity<Any> {
		val response = splitwiseApi.getCurrentUser(key.asJWTToken()).execute()
		if (response.code() == HttpStatus.UNAUTHORIZED.value()) {
			return ResponseEntity.badRequest().body("Invalid API Key!")
		}
		val json = objectMapper.writeValueAsString(response.body())
		val userId = objectMapper.readTree(json).get("user").get("id").asInt()
		val firstName = objectMapper.readTree(json).get("user").get("first_name").asText()
		val lastName = objectMapper.readTree(json).get("user").get("last_name").asText()
		cassandraService.postUserApiKey(UserApiKey(
			user_id = userId.toLong(),
			api_key = key,
			name = "$firstName $lastName",
			created_at = LocalDate.now(),
			updated_at = LocalDate.now()
		))
		return ResponseEntity.ok().body("User Added Successfully!")
	}
}
