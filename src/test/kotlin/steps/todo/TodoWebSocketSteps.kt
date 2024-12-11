package steps.todo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import models.todo.TodoWebSocketMessage
import steps.BaseSteps

class TodoWebSocketSteps : BaseSteps() {

    private val objectMapper = jacksonObjectMapper()

    /**
     * Parses a JSON-formatted WebSocket message into a [TodoWebSocketMessage] object.
     *
     * This method attempts to deserialize the provided JSON string into a [TodoWebSocketMessage].
     * If parsing fails due to malformed JSON or mismatched structure, it logs the error and
     * returns `null`.
     *
     * @param message The JSON-formatted WebSocket message as a [String].
     * @return A [TodoWebSocketMessage] object if parsing is successful; otherwise, `null`.
     */
    fun parseWebSocketMessage(message: String): TodoWebSocketMessage? {
        return try {
            objectMapper.readValue<TodoWebSocketMessage>(message)
        } catch (e: Exception) {
            logger.error("Failed to parse WebSocket message: $message", e)
            null
        }
    }
}