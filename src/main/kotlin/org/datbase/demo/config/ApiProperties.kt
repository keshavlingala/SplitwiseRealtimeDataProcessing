package org.datbase.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "api")
@ConstructorBinding
class ApiProperties(
    val splitwise: SplitwiseProperties
)

data class SplitwiseProperties(
    val url: String,
    val consumerKey: String,
    val consumerSecret: String,
    val initialAccessToken: String
)