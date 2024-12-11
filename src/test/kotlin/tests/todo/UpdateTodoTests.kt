package tests.todo

import constants.ResourceLocks
import enums.HttpStatusCode
import models.todo.CreateTodoRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.ResourceLock
import steps.todo.TodoSteps
import tests.BaseTest
import utils.StringUtils.generateRandomString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateTodoTests : BaseTest() {

    private val todoSteps = TodoSteps()
    private val createdTodoIds: MutableList<Long> = mutableListOf()

    @AfterAll
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun afterAll() {
        createdTodoIds.forEach { todoSteps.deleteTodo(it) }
    }

    /**
     * Test Case 1: Successfully update a Todo with valid data.
     *
     * This test verifies that a Todo can be updated successfully when provided with valid input data.
     * It asserts that the response contains a non-null updated Todo object with the expected properties.
     *
     * Steps:
     * 1. Create a new Todo with valid data using [TodoSteps.createTodo].
     * 2. Assert that the created Todo is not null.
     * 3. Add the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Create an update request with new valid data.
     * 5. Invoke the [TodoSteps.updateTodo] method to update the Todo.
     * 6. Assert that the updated Todo is not null.
     * 7. Perform soft assertions to verify that the Todo's `id`, `text`, and `completed` fields match the updated data.
     */
    @Test
    @DisplayName("PUT /todos/:id - Successfully update a Todo with valid data")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun updateTodoWithValidDataTest() {
        val todo = todoSteps.createTodo(
            CreateTodoRequest(
                text = generateRandomString(),
                completed = false
            )
        )
        todo?.let { createdTodoIds.add(it.id) }

        val updateTodoRequest = CreateTodoRequest(
            text = generateRandomString(),
            completed = true
        )

        val updatedTodo = todoSteps.updateTodo(
            id = todo!!.id,
            todo = updateTodoRequest
        )

        assertThat(updatedTodo)
            .isNotNull
        assertSoftly {
            it.assertThat(updatedTodo!!.id)
                .isEqualTo(todo.id)
            it.assertThat(updatedTodo.text)
                .isEqualTo(updateTodoRequest.text)
            it.assertThat(updatedTodo.completed)
                .isEqualTo(updateTodoRequest.completed)
        }
    }

    /**
     * Test Case 2: Attempt to update a non-existing Todo (expecting 404 Not Found).
     *
     * This test verifies that attempting to update a Todo with an ID that does not exist
     * results in a 404 Not Found status code. It ensures that the API correctly handles
     * update requests for non-existent resources.
     *
     * Steps:
     * 1. Retrieve a non-existing Todo ID using [TodoSteps.getNonExistingTodoId].
     * 2. Attempt to update the non-existing Todo using [TodoSteps.updateTodo] with the expected status code.
     */
    @Test
    @DisplayName("PUT /todos/:id - Updating a non-existing Todo should return 404 Not Found")
    fun updateNonExistingTodoTest() {
        val nonExistingId = todoSteps.getNonExistingTodoId()

        todoSteps.updateTodo(
            id = nonExistingId,
            todo = CreateTodoRequest(
                text = generateRandomString(),
                completed = true
            ),
            expectedStatusCode = HttpStatusCode.NOT_FOUND
        )
    }

    /**
     * Test Case 3: Attempt to update a Todo with an empty 'text' field (expecting 400 Bad Request).
     *
     * This test verifies that updating a Todo with an empty 'text' field is rejected by the API,
     * resulting in a 400 Bad Request status code. It ensures that the API enforces validation rules
     * on the 'text' field during updates.
     *
     * Steps:
     * 1. Create a new Todo with valid data using [TodoSteps.createTodo].
     * 2. Assert that the created Todo is not null.
     * 3. Add the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Attempt to update the Todo with an empty 'text' field using [TodoSteps.updateTodo] with the expected status code.
     */
    @Test
    @DisplayName("PUT /todos/:id - Updating a Todo with an empty 'text' field should return 400 Bad Request")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun updateTodoWithEmptyTextTest() {
        val todo = todoSteps.createTodo(
            CreateTodoRequest(
                text = generateRandomString(),
                completed = false
            )
        )
        todo?.let { createdTodoIds.add(it.id) }

        todoSteps.updateTodo(
            id = todo!!.id,
            todo = CreateTodoRequest(
                text = "   ",
                completed = true
            ),
            expectedStatusCode = HttpStatusCode.BAD_REQUEST
        )
    }

    /**
     * Test Case 4: Attempt to update a Todo with text exceeding the maximum length (expecting 400 Bad Request).
     *
     * This test verifies that updating a Todo with a 'text' field that exceeds the maximum allowed length
     * is rejected by the API, resulting in a 400 Bad Request status code. It ensures that the API enforces
     * validation rules on the 'text' field length during updates.
     *
     * Steps:
     * 1. Create a new Todo with valid data using [TodoSteps.createTodo].
     * 2. Assert that the created Todo is not null.
     * 3. Add the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Attempt to update the Todo with an excessively long 'text' field using [TodoSteps.updateTodo] with the expected status code.
     */
    @Test
    @DisplayName("PUT /todos/:id - Updating a Todo with excessive text length should return 400 Bad Request")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun updateTodoWithExcessiveTextLengthTest() {
        val todo = todoSteps.createTodo(
            CreateTodoRequest(
                text = generateRandomString(),
                completed = false
            )
        )
        todo?.let { createdTodoIds.add(it.id) }

        todoSteps.updateTodo(
            id = todo!!.id,
            todo = CreateTodoRequest(
                text = generateRandomString(256),
                completed = true
            ),
            expectedStatusCode = HttpStatusCode.BAD_REQUEST
        )
    }

    /**
     * Test Case 5: Attempt to update a Todo without required fields ('text' and 'completed') (expecting 400 Bad Request).
     *
     * This test verifies that updating a Todo without specifying the required fields
     * 'text' and 'completed' is rejected by the API, resulting in a 400 Bad Request status code.
     * It ensures that the API enforces the presence of mandatory fields during updates.
     *
     * Steps:
     * 1. Create a new Todo with valid data using [TodoSteps.createTodo].
     * 2. Assert that the created Todo is not null.
     * 3. Add the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Attempt to update the Todo without specifying required fields using [TodoSteps.updateTodo] with the expected status code.
     */
    @Test
    @DisplayName("PUT /todos/:id - Updating a Todo without required fields should return 400 Bad Request")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun updateTodoWithoutRequiredFieldsTest() {
        val todo = todoSteps.createTodo(
            CreateTodoRequest(
                text = generateRandomString(),
                completed = false
            )
        )
        todo?.let { createdTodoIds.add(it.id) }

        todoSteps.updateTodo(
            id = todo!!.id,
            todo = CreateTodoRequest(),
            expectedStatusCode = HttpStatusCode.BAD_REQUEST
        )
    }

    // TODO: Test Case 6: Attempt to update a Todo with an invalid ID (e.g., string instead of numeric) - expecting 400 Bad Request or 404 Not Found.
    // TODO: Test Case 7: Verify that updating a Todo does not alter other fields (if there are additional fields)
    // TODO: Test Case 8: Verify updating a Todo with special characters in the text
    // TODO: Test Case 9: Verify transaction rollback upon update failure (if supported)
    // TODO: Test Case 10: Verify API behavior when simultaneously updating the same Todo from different threads
}