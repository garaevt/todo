package services

import api.todo.TodoApi
import enums.HttpStatusCode
import enums.HttpStatusCode.OK
import enums.HttpStatusCode.CREATED
import enums.HttpStatusCode.NOT_FOUND
import enums.HttpStatusCode.BAD_REQUEST
import exceptions.ApiException
import models.todo.CreateTodoRequest
import models.todo.Todo

/**
 * Service class for managing Todo operations.
 *
 * This class provides methods to interact with Todo-related API endpoints,
 * including creating, retrieving, updating, and deleting Todos. It leverages
 * the [TodoApi] for HTTP interactions and extends [BaseService] to utilize
 * common validation functionalities.
 */
class TodoService(private val todoApi: TodoApi = TodoApi()) : BaseService() {

    /**
     * Retrieves a list of Todos with optional pagination parameters.
     *
     * @param offset The offset for pagination. If null, no offset is applied.
     * @param limit The maximum number of Todos to retrieve. If null, the default limit is used.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response.
     * @return A [List] of [Todo] objects.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun getTodos(offset: Int? = null, limit: Int? = null, expectedStatusCode: HttpStatusCode): List<Todo> {
        val response = todoApi.getTodos(offset, limit)
        validateResponse(response, expectedStatusCode)
        return response.jsonPath().getList("", Todo::class.java)
    }

    /**
     * Creates a new Todo.
     *
     * @param todo The [CreateTodoRequest] object containing the details of the Todo to be created.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response.
     * @return The created [Todo] object if the creation is successful; otherwise, null.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun createTodo(todo: CreateTodoRequest, expectedStatusCode: HttpStatusCode): Todo? {
        val response = todoApi.createTodo(todo)
        validateResponse(response, expectedStatusCode)
        return when (response.statusCode) {
            CREATED.code -> response.jsonPath().getObject("", Todo::class.java)
            BAD_REQUEST.code -> null
            else -> throw ApiException("Unexpected status code: ${response.statusCode}")
        }
    }

    /**
     * Updates an existing Todo identified by its ID.
     *
     * @param id The ID of the Todo to update.
     * @param todo The [CreateTodoRequest] object containing the updated data for the Todo.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response.
     * @return The updated [Todo] object if the update is successful; otherwise, null.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun updateTodo(id: Long, todo: CreateTodoRequest, expectedStatusCode: HttpStatusCode): Todo? {
        val response = todoApi.updateTodo(id, todo)
        validateResponse(response, expectedStatusCode)
        return when (response.statusCode) {
            OK.code -> response.jsonPath().getObject("", Todo::class.java)
            NOT_FOUND.code, BAD_REQUEST.code -> null
            else -> throw ApiException("Unexpected status code: ${response.statusCode}")
        }
    }

    /**
     * Deletes a Todo identified by its ID.
     *
     * @param id The ID of the Todo to delete.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun deleteTodo(id: Long, expectedStatusCode: HttpStatusCode) {
        val response = todoApi.deleteTodo(id)
        validateResponse(response, expectedStatusCode)
    }

    /**
     * Deletes a Todo identified by its ID without authentication.
     * This method is intended for negative testing scenarios where authentication is required.
     *
     * @param id The ID of the Todo to delete.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response.
     * @throws ApiException if the response status code does not match the expectation.
     */
    fun deleteTodoUnauthenticated(id: Long, expectedStatusCode: HttpStatusCode) {
        val response = todoApi.deleteTodoUnauthenticated(id)
        validateResponse(response, expectedStatusCode)
    }
}