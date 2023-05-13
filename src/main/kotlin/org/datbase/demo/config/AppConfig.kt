package org.datbase.demo.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.kafka.clients.admin.NewTopic
import org.datbase.demo.retrofit.SplitwiseApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.net.InetSocketAddress
import com.datastax.oss.driver.api.core.CqlSession as CqlSession1

@Configuration
class AppConfig(
	val apiProperties: ApiProperties,
) {
	@Suppress("deprecation")
	@Bean
	fun objectMapper(): ObjectMapper {
		return ObjectMapper().registerModule(JavaTimeModule())
			.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
			.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
			.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
			.registerModule(KotlinModule.Builder().build())
			.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
	}

	@Bean
	fun session(
		@Value("\${spring.data.cassandra.keyspace-name}") keyspaceName: String,
		@Value("\${spring.data.cassandra.contact-points}") contactPoints: String,
		@Value("\${spring.data.cassandra.port}") port: Int
	): CqlSession1 {
		return CqlSession1.builder()
			.withKeyspace(keyspaceName)
			.withLocalDatacenter("datacenter1")
			.addContactPoint(InetSocketAddress(contactPoints, port))
			.build()
	}

	@Bean
	fun splitwiseApi(): SplitwiseApi {
		val (url, _, _, _) = apiProperties.splitwise
		val interceptor = HttpLoggingInterceptor()
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
		val client = OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.addInterceptor { chain ->
				val request = chain.request().newBuilder()
					.build()
				chain.proceed(request)
			}
			.build()
		val retrofit = Retrofit.Builder()
			.baseUrl(url)
			.addConverterFactory(JacksonConverterFactory.create(objectMapper()))
			.client(client)
			.client(
				OkHttpClient.Builder()
					.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
					.build()
			)
			.build()

		return retrofit.create(SplitwiseApi::class.java)
	}

	@Bean
	fun individualTopic(): NewTopic {
		return TopicBuilder.name("expenses")
			.partitions(3)
			.replicas(1)
			.build()
	}

	@Bean
	fun ping(): NewTopic {
		return TopicBuilder.name("ping")
			.partitions(1)
			.replicas(1)
			.build()
	}

	@Bean
	fun test(): NewTopic {
		return TopicBuilder.name("test")
			.partitions(1)
			.replicas(1)
			.build()
	}
}