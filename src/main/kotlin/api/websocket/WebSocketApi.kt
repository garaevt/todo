package api.websocket

import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Request
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.TimeUnit

/**
 * API client for managing WebSocket connections.
 */
class WebSocketApi {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null

    /**
     * Establishes a connection to the WebSocket server.
     *
     * @param url The URL of the WebSocket server.
     * @param listener The [WebSocketListener] to handle WebSocket events.
     */
    fun connect(url: String, listener: WebSocketListener) {
        val request = Request.Builder()
            .url(url)
            .build()
        webSocket = client.newWebSocket(request, listener)
        logger.info("Attempting to connect to WebSocket server at $url")
    }

    /**
     * Sends a message through the established WebSocket connection.
     *
     * @param message The message to send.
     * @return `true` if the message was successfully sent; `false` otherwise.
     */
    fun sendMessage(message: String): Boolean {
        val result = webSocket?.send(message) ?: false
        if (result) {
            logger.info("Sent message: $message")
        } else {
            logger.error("Failed to send message: $message. WebSocket is not connected.")
        }
        return result
    }

    /**
     * Closes the WebSocket connection.
     *
     * @param code The closure code (default is `1000` for normal closure).
     * @param reason The reason for closing the connection (default is "Normal closure").
     */
    fun close(code: Int = 1000, reason: String = "Normal closure") {
        if (webSocket != null) {
            webSocket?.close(code, reason)
            logger.info("Closing WebSocket connection. Code: $code, Reason: $reason")
        } else {
            logger.warn("Attempted to close WebSocket connection, but it was not established.")
        }
    }
}