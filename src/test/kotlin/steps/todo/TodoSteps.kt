package steps.todo

import enums.HttpStatusCode
import enums.PageSize
import exceptions.ApiException
import models.todo.CreateTodoRequest
import models.todo.Todo
import services.TodoService
import steps.BaseSteps

/**
 * Steps class for performing Todo-related operations in tests or workflows.
 *
 * This class provides high-level methods to interact with Todo API endpoints
 * through the [TodoService]. It extends [BaseSteps] to inherit common
 * functionalities required across different steps classes.
 */
class TodoSteps(private val todoService: TodoService = TodoService()) : BaseSteps() {

    /**
     * Retrieves a list of Todos with optional pagination parameters.
     *
     * @param offset The offset for pagination. If null, no offset is applied.
     * @param limit The maximum number of Todos to retrieve. If null, the default limit is used.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response. Defaults to [HttpStatusCode.OK].
     * @return A [List] of [Todo] objects.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun getTodos(
        offset: Int? = null,
        limit: Int? = null,
        expectedStatusCode: HttpStatusCode = HttpStatusCode.OK
    ): List<Todo> {
        return todoService.getTodos(offset, limit, expectedStatusCode)
    }

    /**
     * Creates a new Todo.
     *
     * @param todo The [CreateTodoRequest] object containing the details of the Todo to be created.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response. Defaults to [HttpStatusCode.CREATED].
     * @return The created [Todo] object if the creation is successful; otherwise, null.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun createTodo(
        todo: CreateTodoRequest,
        expectedStatusCode: HttpStatusCode = HttpStatusCode.CREATED
    ): Todo? {
        return todoService.createTodo(todo, expectedStatusCode)
    }

    /**
     * Updates an existing Todo identified by its ID.
     *
     * @param id The ID of the Todo to update.
     * @param todo The [CreateTodoRequest] object containing the updated data for the Todo.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response. Defaults to [HttpStatusCode.OK].
     * @return The updated [Todo] object if the update is successful; otherwise, null.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun updateTodo(
        id: Long,
        todo: CreateTodoRequest,
        expectedStatusCode: HttpStatusCode = HttpStatusCode.OK
    ): Todo? {
        return todoService.updateTodo(id, todo, expectedStatusCode)
    }

    /**
     * Deletes a Todo identified by its ID.
     *
     * @param id The ID of the Todo to delete.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response. Defaults to [HttpStatusCode.NO_CONTENT].
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun deleteTodo(
        id: Long,
        expectedStatusCode: HttpStatusCode = HttpStatusCode.NO_CONTENT
    ) {
        todoService.deleteTodo(id, expectedStatusCode)
    }

    /**
     * Deletes a Todo identified by its ID without authentication.
     * This method is intended for negative testing scenarios where authentication is required.
     *
     * @param id The ID of the Todo to delete.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response. Defaults to [HttpStatusCode.UNAUTHORIZED].
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun deleteTodoUnauthenticated(
        id: Long,
        expectedStatusCode: HttpStatusCode = HttpStatusCode.UNAUTHORIZED
    ) {
        todoService.deleteTodoUnauthenticated(id, expectedStatusCode)
    }

    /**
     * Generates a non-existing Todo ID by incrementing the highest existing ID.
     *
     * @return A [Long] representing a non-existing Todo ID.
     * @throws ApiException if there are issues retrieving existing Todos.
     */
    fun getNonExistingTodoId(): Long {
        return (getTodos().maxOfOrNull { it.id } ?: 0L) + 1L
    }

    /**
     * Retrieves all Todos by paginating through the API.
     *
     * @param batchSize The number of Todos to fetch per request. Defaults to 50.
     * @return A [List] of all [Todo] objects.
     * @throws ApiException if any paginated request fails.
     */
    fun getAllTodos(batchSize: Int = PageSize._50.size): List<Todo> {
        val allTodos = mutableListOf<Todo>()
        var offset = 0

        while (true) {
            val todosBatch = getTodos(offset = offset, limit = batchSize)
            if (todosBatch.isEmpty()) {
                break
            }
            allTodos.addAll(todosBatch)
            if (todosBatch.size < batchSize) {
                break
            }
            offset += batchSize
        }

        return allTodos
    }
}