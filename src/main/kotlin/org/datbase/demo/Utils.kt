package org.datbase.demo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode

fun String.asJWTToken(): String {
	return "Bearer $this"
}
