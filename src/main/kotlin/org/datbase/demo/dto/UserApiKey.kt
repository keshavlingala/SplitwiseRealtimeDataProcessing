package org.datbase.demo.dto

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDate

@Table("user_api_keys")
data class UserApiKey(
	@PrimaryKey
	val user_id: Long,
	val name: String,
	val api_key: String,
	val created_at: LocalDate,
	val updated_at: LocalDate,
)
