package tests.todo

import constants.ResourceLocks
import enums.HttpStatusCode
import models.todo.CreateTodoRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.ResourceLock
import steps.todo.TodoSteps
import tests.BaseTest
import utils.StringUtils.generateRandomString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeleteTodoTests : BaseTest() {

    private val todoSteps = TodoSteps()
    private val createdTodoIds: MutableList<Long> = mutableListOf()

    @AfterAll
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun afterAll() {
        createdTodoIds.forEach {
            todoSteps.deleteTodo(it)
        }
    }

    /**
     * Test Case 1: Successfully delete an existing Todo.
     *
     * This test verifies that a Todo can be deleted successfully when provided with a valid Todo ID.
     * It performs the following steps:
     *
     * 1. Creates a new Todo with valid data.
     * 2. Asserts that the created Todo is not null.
     * 3. Adds the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Deletes the Todo using its ID.
     * 5. Removes the Todo's ID from the [createdTodoIds] list since it's been deleted.
     * 6. Asserts that the Todo no longer exists in the list of Todos.
     */
    @Test
    @DisplayName("DELETE /todos/{id} - Successfully delete an existing Todo")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun deleteExistingTodoTest() {
        val createTodoRequest = CreateTodoRequest(
            text = generateRandomString(10),
            completed = false
        )
        val todo = todoSteps.createTodo(createTodoRequest)

        assertThat(todo).isNotNull

        if (todo != null) {
            createdTodoIds.add(todo.id)
            todoSteps.deleteTodo(id = todo.id, expectedStatusCode = HttpStatusCode.NO_CONTENT)
            createdTodoIds.remove(todo.id)

            assertThat(todoSteps.getTodos().firstOrNull { it.id == todo.id })
                .describedAs("Deleted Todo with ID ${todo.id} should no longer exist")
                .isNull()
        }
    }

    /**
     * Test Case 2: Attempt to delete a non-existing Todo (expecting 404 Not Found).
     *
     * This test verifies that attempting to delete a Todo with an ID that does not exist
     * results in a 404 Not Found status code. It ensures that the API correctly handles
     * deletion requests for non-existent resources.
     */
    @Test
    @DisplayName("DELETE /todos/{id} - Attempt to delete a non-existing Todo (Expecting 404)")
    fun deleteNonExistingTodoTest() {
        todoSteps.deleteTodo(id = todoSteps.getNonExistingTodoId(), expectedStatusCode = HttpStatusCode.NOT_FOUND)
    }

    /**
     * Test Case 3: Attempt to delete a Todo without authorization (expecting 401 Unauthorized).
     *
     * This test verifies that the API does not allow deleting a Todo without proper authorization.
     * It performs the following steps:
     *
     * 1. Creates a new Todo with valid data.
     * 2. Asserts that the created Todo is not null.
     * 3. Adds the Todo's ID to the [createdTodoIds] list for cleanup.
     * 4. Attempts to delete the Todo without authorization.
     */
    @Test
    @DisplayName("DELETE /todos/{id} - Attempt to delete a Todo without authorization (Expecting 401)")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun deleteTodoWithoutAuthTest() {
        val todo = todoSteps.createTodo(
            CreateTodoRequest(
                text = generateRandomString(10),
                completed = false
            )
        )

        assertThat(todo).isNotNull

        if (todo != null) {
            createdTodoIds.add(todo.id)
            todoSteps.deleteTodoUnauthenticated(id = todo.id)
        }
    }

    /**
     * Test Case 4: Attempt to delete a Todo that has already been deleted (expecting 404 Not Found).
     *
     * This test verifies that attempting to delete a Todo that has already been deleted
     * results in a 404 Not Found status code. It ensures that the API correctly handles
     * deletion requests for resources that have been removed.
     */
    @Test
    @DisplayName("DELETE /todos/{id} - Attempt to delete an already deleted Todo (Expecting 404)")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun deleteAlreadyDeletedTodoTest() {
        val createTodoRequest = CreateTodoRequest(
            text = generateRandomString(10),
            completed = false
        )
        val todo = todoSteps.createTodo(createTodoRequest)

        assertThat(todo).isNotNull

        if (todo != null) {
            createdTodoIds.add(todo.id)
            todoSteps.deleteTodo(id = todo.id, expectedStatusCode = HttpStatusCode.NO_CONTENT)
            createdTodoIds.remove(todo.id)
            todoSteps.deleteTodo(id = todo.id, expectedStatusCode = HttpStatusCode.NOT_FOUND)
        }
    }

    // TODO: Test Case 5: Attempt to delete a Todo without specifying ID (expecting 400 Bad Request or 404)
    // TODO: Test Case 6: Attempt to delete a Todo with invalid ID format (expecting 400 Bad Request).
    // TODO: Test Case 7: Attempt to delete a Todo belonging to another user (expecting 403 Forbidden)
    // TODO: Test Case 8: Successfully delete multiple Todos in succession
    // TODO: Test Case 9: Attempt to delete a Todo with duplicate 'text' (if uniqueness is enforced)
    // TODO: Test Case 10: Verify response headers and status codes upon successful deletion
}