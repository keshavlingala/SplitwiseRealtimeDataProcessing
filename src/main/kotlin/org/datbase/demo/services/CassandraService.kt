package org.datbase.demo.services

import org.datbase.demo.dto.UserApiKey
import org.datbase.demo.retrofit.CassandraRepository
import org.springframework.stereotype.Service

@Service
class CassandraService(
	private val cassandraRepository: CassandraRepository,
) {
	fun postUserApiKey(userApiKey: UserApiKey) {
		cassandraRepository.save(userApiKey)
	}

	fun getAllUserApiKeys(): List<UserApiKey> {
		return cassandraRepository.findAll().toList()
	}
}