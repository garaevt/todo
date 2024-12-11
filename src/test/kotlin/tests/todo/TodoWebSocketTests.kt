package tests.todo

import config.Config
import constants.ResourceLocks
import enums.HttpStatusCode
import enums.WebsocketMessageType
import io.qameta.allure.*
import models.todo.CreateTodoRequest
import models.todo.TodoWebSocketMessage
import org.awaitility.Awaitility.await
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.ResourceLock
import services.websocket.WebSocketService
import steps.todo.TodoSteps
import steps.todo.TodoWebSocketSteps
import tests.BaseTest
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import utils.StringUtils.generateRandomString

@Feature("WebSocket Testing")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoWebSocketTests : BaseTest() {

    private val createdTodoIds: MutableList<Long> = mutableListOf()
    private val todoSteps = TodoSteps()
    private var todoWebSocketSteps = TodoWebSocketSteps()
    private lateinit var webSocketService: WebSocketService

    private val receivedWebSocketMessage = AtomicReference<TodoWebSocketMessage?>()

    @BeforeAll
    fun setUp() {
        webSocketService = WebSocketService()
        webSocketService.addMessageListener { message ->
            val parsedMessage = todoWebSocketSteps.parseWebSocketMessage(message)
            parsedMessage?.let { receivedWebSocketMessage.set(it) }
        }
        webSocketService.connect("${Config.TODO_WS_URL}/ws")
    }

    @AfterAll
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun tearDown() {
        createdTodoIds.forEach { todoSteps.deleteTodo(it) }
        webSocketService.closeConnection()
    }

    /**
     * Test Case 1: Receive a 'new_todo' message upon creating a new Todo.
     *
     * This test verifies that when a new Todo is created via the API, a corresponding
     * WebSocket message of type 'NEW_TODO' is received with the correct Todo data.
     *
     * Steps:
     * 1. Create a new Todo with valid data using [TodoSteps.createTodo].
     * 2. Assert that the created Todo is not null.
     * 3. Add the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Wait for up to 5 seconds for a 'NEW_TODO' WebSocket message to be received.
     * 5. Assert that the received message type is 'NEW_TODO'.
     * 6. Assert that the message data matches the created Todo's details.
     */
    @Test
    @DisplayName("WebSocket - Receive 'NEW_TODO' message upon creating a new Todo")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun newTodoWebsocketTest() {
        val createTodoRequest = CreateTodoRequest(
            text = generateRandomString(),
            completed = false
        )
        val todo = todoSteps.createTodo(createTodoRequest)
        assertThat(todo).isNotNull
        todo?.let { createdTodoIds.add(it.id) }

        await().atMost(5, TimeUnit.SECONDS).until {
            val message = receivedWebSocketMessage.get()
            message != null && message.type == WebsocketMessageType.NEW_TODO.type
        }

        val message = receivedWebSocketMessage.get()
        assertThat(message)
            .isNotNull
            .extracting("type")
            .isEqualTo(WebsocketMessageType.NEW_TODO.type)

        assertThat(message?.data)
            .isNotNull
            .extracting("id", "text", "completed")
            .containsExactly(todo!!.id, createTodoRequest.text, createTodoRequest.completed)
    }

    /**
     * Test Case 2: Receive an 'update_todo' message upon updating an existing Todo.
     *
     * This test verifies that when an existing Todo is updated via the API, a corresponding
     * WebSocket message of type 'UPDATE_TODO' is received with the updated Todo data.
     *
     * Steps:
     * 1. Create a new Todo with valid data using [TodoSteps.createTodo].
     * 2. Assert that the created Todo is not null.
     * 3. Add the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Update the Todo's details using [TodoSteps.updateTodo].
     * 5. Wait for up to 5 seconds for an 'UPDATE_TODO' WebSocket message to be received.
     * 6. Assert that the received message type is 'UPDATE_TODO'.
     * 7. Assert that the message data matches the updated Todo's details.
     */
    @Test
    @DisplayName("WebSocket - Receive 'UPDATE_TODO' message upon updating an existing Todo")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun updateTodoWebsocketTest() {
        val createTodoRequest = CreateTodoRequest(
            text = generateRandomString(),
            completed = false
        )
        val todo = todoSteps.createTodo(createTodoRequest)
        assertThat(todo).isNotNull
        todo?.let { createdTodoIds.add(it.id) }

        val updateTodoRequest = CreateTodoRequest(
            text = generateRandomString(),
            completed = true
        )

        val updatedTodo = todoSteps.updateTodo(
            id = todo!!.id,
            todo = updateTodoRequest
        )

        await().atMost(5, TimeUnit.SECONDS).until {
            val message = receivedWebSocketMessage.get()
            message != null && message.type == WebsocketMessageType.UPDATE_TODO.type
        }

        val message = receivedWebSocketMessage.get()
        assertThat(message)
            .isNotNull
            .extracting("type")
            .isEqualTo(WebsocketMessageType.UPDATE_TODO.type)

        assertThat(message?.data)
            .isNotNull
            .extracting("id", "text", "completed")
            .containsExactly(updatedTodo!!.id, updateTodoRequest.text, updateTodoRequest.completed)
    }

    /**
     * Test Case 3: Receive a 'delete_todo' message upon deleting an existing Todo.
     *
     * This test verifies that when an existing Todo is deleted via the API, a corresponding
     * WebSocket message of type 'DELETE_TODO' is received with the correct Todo ID.
     *
     * Steps:
     * 1. Create a new Todo with valid data using [TodoSteps.createTodo].
     * 2. Assert that the created Todo is not null.
     * 3. Add the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Delete the Todo using [TodoSteps.deleteTodo].
     * 5. Wait for up to 5 seconds for a 'DELETE_TODO' WebSocket message to be received.
     * 6. Assert that the received message type is 'DELETE_TODO'.
     * 7. Assert that the message data contains the correct Todo ID.
     */
    @Test
    @DisplayName("WebSocket - Receive 'DELETE_TODO' message upon deleting an existing Todo")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun deleteTodoWebsocketTest() {
        val createTodoRequest = CreateTodoRequest(
            text = generateRandomString(),
            completed = false
        )
        val todo = todoSteps.createTodo(createTodoRequest)
        assertThat(todo).isNotNull
        todo?.let { createdTodoIds.add(it.id) }

        todoSteps.deleteTodo(
            id = todo!!.id,
        )

        await().atMost(5, TimeUnit.SECONDS).until {
            val message = receivedWebSocketMessage.get()
            message != null && message.type == WebsocketMessageType.DELETE_TODO.type
        }

        val message = receivedWebSocketMessage.get()
        assertThat(message)
            .isNotNull
            .extracting("type")
            .isEqualTo(WebsocketMessageType.DELETE_TODO.type)

        assertThat(message?.data)
            .isNotNull
            .extracting("id")
            .isEqualTo(todo.id)
    }

    /**
     * Test Case 5: Verify correct handling of multiple consecutive WebSocket messages.
     *
     * This test verifies that the system can correctly handle multiple WebSocket messages in sequence,
     * such as creating multiple Todos, updating one, and deleting another. It ensures that each
     * message is received and processed in the correct order.
     *
     * Steps:
     * 1. Create two new Todos using [TodoSteps.createTodo].
     * 2. Assert that both Todos are not null.
     * 3. Add both Todos' IDs to the [createdTodoIds] list for cleanup.
     * 4. Update the first Todo's details using [TodoSteps.updateTodo].
     * 5. Delete the second Todo using [TodoSteps.deleteTodo].
     * 6. Define the expected sequence of WebSocket message types.
     * 7. Add an additional message listener to capture all received message types.
     * 8. Wait for up to 10 seconds for all expected messages to be received.
     * 9. Assert that all expected message types are present in the received messages.
     */
    @Test
    @DisplayName("WebSocket - Correctly handle multiple consecutive WebSocket messages")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun `should correctly handle multiple websocket messages in sequence`() {
        val todo1 = todoSteps.createTodo(
            CreateTodoRequest(
                text = generateRandomString(),
                completed = false
            )
        )
        assertThat(todo1).isNotNull
        todo1?.let { createdTodoIds.add(it.id) }

        val todo2 = todoSteps.createTodo(
            CreateTodoRequest(
                text = generateRandomString(),
                completed = false
            )
        )
        assertThat(todo2).isNotNull
        todo2?.let { createdTodoIds.add(it.id) }

        val updateTodoRequest1 = CreateTodoRequest(
            text = generateRandomString(),
            completed = true
        )
        todoSteps.updateTodo(
            id = todo1!!.id,
            todo = updateTodoRequest1
        )

        todoSteps.deleteTodo(
            id = todo2!!.id,
            expectedStatusCode = HttpStatusCode.NO_CONTENT
        )

        val expectedMessages = listOf(
            WebsocketMessageType.NEW_TODO.type,
            WebsocketMessageType.NEW_TODO.type,
            WebsocketMessageType.UPDATE_TODO.type,
            WebsocketMessageType.DELETE_TODO.type
        )

        val receivedMessageTypes = mutableListOf<String>()

        webSocketService.addMessageListener { message ->
            val parsedMessage = todoWebSocketSteps.parseWebSocketMessage(message)
            parsedMessage?.let { receivedMessageTypes.add(it.type) }
        }

        await().atMost(10, TimeUnit.SECONDS).until {
            receivedMessageTypes.containsAll(expectedMessages)
        }

        assertThat(receivedMessageTypes)
            .containsAll(expectedMessages)
    }

    // TODO: Test Case 6: Verify handling of incorrect or malformed messages
    // TODO: Test Case 7: Verify WebSocket connection recovery upon disconnection
    // TODO: Test Case 8: Verify performance under high message volume
    // TODO: Test Case 9: Verify security of the WebSocket connection (e.g., authorization)
    // TODO: Test Case 10: Verify compatibility with different WebSocket client versions
}