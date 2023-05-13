package org.datbase.demo.retrofit

import org.datbase.demo.dto.UserApiKey
import org.springframework.data.repository.CrudRepository
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.math.BigInteger

interface CassandraRepository : CrudRepository<UserApiKey, BigInteger> {

}