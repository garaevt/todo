package steps

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

open class BaseSteps {

    val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * Logs a trace-level message.
     *
     * @param message The message to log.
     */
    fun logTrace(message: String) {
        logger.trace(message)
    }

    /**
     * Logs a debug-level message.
     *
     * @param message The message to log.
     */
    fun logDebug(message: String) {
        logger.debug(message)
    }

    /**
     * Logs an info-level message.
     *
     * @param message The message to log.
     */
    fun logInfo(message: String) {
        logger.info(message)
    }

    /**
     * Logs an error-level message.
     *
     * @param message The message to log.
     */
    fun logError(message: String) {
        logger.error(message)
    }
}