package config

import enums.Environment
import exceptions.ConfigurationException
import java.util.Properties
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Config {
    private val properties = Properties()
    private val logger: Logger = LogManager.getLogger(this::class.java)

    init {
        val environment = System.getProperty("env") ?: Environment.LOCAL.environment
        val configFileName = "application-$environment.properties"
        val inputStream = this::class.java.classLoader.getResourceAsStream(configFileName)
            ?: throw ConfigurationException("Configuration file $configFileName not found")

        inputStream.use { stream ->
            try {
                properties.load(stream)
                logger.info("Loaded configuration from $configFileName")
            } catch (e: Exception) {
                logger.error("Failed to load properties from $configFileName", e)
                throw ConfigurationException("Failed to load properties from $configFileName", e)
            }
        }
    }

    val TODO_URI: String = properties.getProperty("todo.uri")
        ?: throw RuntimeException("todo.uri is missing")
    val TODO_WS_URL: String = properties.getProperty("todo.ws.url")
        ?: throw RuntimeException("ws.url is missing")
    val AUTH_HEADER: String = properties.getProperty("auth.header")
        ?: throw RuntimeException("auth.header is missing")
    val TIMEOUT_SECONDS: Int = properties.getProperty("timeout.seconds")?.toInt()
        ?: throw RuntimeException("timeout.seconds is missing")
}