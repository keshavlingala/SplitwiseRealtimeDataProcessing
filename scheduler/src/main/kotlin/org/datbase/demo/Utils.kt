package org.datbase.demo

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun String.asJWTToken(): String {
	return "Bearer $this"
}

inline fun <reified T, reified R> Collection<T>.multiAsync(
	crossinline block: (T) -> R,
): List<R> = runBlocking {
	map { async { block(it) } }.awaitAll()
}