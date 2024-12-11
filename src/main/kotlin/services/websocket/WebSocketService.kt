package services.websocket

import api.websocket.WebSocketApi
import okhttp3.WebSocketListener
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Service class for managing WebSocket connections and handling WebSocket events.
 *
 * This class leverages [WebSocketApi] to establish and manage connections,
 * send messages, and handle incoming messages through registered listeners.
 */
class WebSocketService : WebSocketListener() {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val webSocketApi: WebSocketApi = WebSocketApi()

    private var isConnected: Boolean = false

    private val messageListeners: MutableList<(String) -> Unit> = mutableListOf()

    /**
     * Connects to the specified WebSocket server URL.
     *
     * @param url The URL of the WebSocket server.
     */
    fun connect(url: String) {
        webSocketApi.connect(url, this)
        logger.info("Initiated WebSocket connection to $url")
    }

    /**
     * Sends a message through the established WebSocket connection.
     *
     * @param message The message to send.
     */
    fun sendMessage(message: String) {
        if (isConnected) {
            val success = webSocketApi.sendMessage(message)
            if (success) {
                logger.info("Sent message: $message")
            } else {
                logger.error("Failed to send message: $message")
            }
        } else {
            logger.error("Cannot send message. WebSocket is not connected.")
        }
    }

    /**
     * Closes the WebSocket connection.
     *
     * @param code The closure code (default is `1000` for normal closure).
     * @param reason The reason for closing the connection (default is "Normal closure").
     */
    fun closeConnection(code: Int = 1000, reason: String = "Normal closure") {
        webSocketApi.close(code, reason)
        logger.info("Closed WebSocket connection with code $code and reason '$reason'")
    }

    /**
     * Adds a listener for incoming WebSocket messages.
     *
     * @param listener A function that will be invoked when a message is received.
     */
    fun addMessageListener(listener: (String) -> Unit) {
        messageListeners.add(listener)
    }

    /**
     * Called when the WebSocket connection is successfully opened.
     *
     * @param webSocket The WebSocket instance.
     * @param response The initial response from the server.
     */
    override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
        logger.info("WebSocket connection opened: $response")
        isConnected = true
    }

    /**
     * Called when a text message is received from the WebSocket server.
     *
     * @param webSocket The WebSocket instance.
     * @param text The received text message.
     */
    override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
        logger.info("Received message: $text")
        messageListeners.forEach { it.invoke(text) }
    }

    /**
     * Called when the WebSocket connection is about to close.
     *
     * @param webSocket The WebSocket instance.
     * @param code The closure code.
     * @param reason The reason for closing the connection.
     */
    override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        logger.info("WebSocket is closing: Code=$code, Reason=$reason")
        isConnected = false
    }

    /**
     * Called when the WebSocket connection has been closed.
     *
     * @param webSocket The WebSocket instance.
     * @param code The closure code.
     * @param reason The reason for closing the connection.
     */
    override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        logger.info("WebSocket closed: Code=$code, Reason=$reason")
        isConnected = false
    }

    /**
     * Called when there is a failure in the WebSocket connection.
     *
     * @param webSocket The WebSocket instance.
     * @param t The throwable representing the failure.
     * @param response The response from the server, if any.
     */
    override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: okhttp3.Response?) {
        logger.error("WebSocket failure: ${t.message}", t)
        isConnected = false
    }
}