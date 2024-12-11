package api.todo

import api.BaseApi
import config.Config
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import models.todo.CreateTodoRequest

/**
 * API client for interacting with the Todo endpoints.
 */
class TodoApi : BaseApi() {
    private val baseUri = Config.TODO_URI

    /**
     * The authenticated specification used for making authenticated requests.
     * It includes the Authorization header.
     */
    private val authenticatedSpec: RequestSpecification by lazy {
        givenBase()
            .header("Authorization", Config.AUTH_HEADER)
    }

    /**
     * Retrieves a list of Todos with optional pagination parameters.
     *
     * @param offset The offset for pagination. If null, no offset is applied.
     * @param limit The maximum number of Todos to retrieve. If null, default limit is used.
     * @return The [Response] from the GET /todos endpoint.
     */
    fun getTodos(offset: Int? = null, limit: Int? = null): Response {
        val queryParams = mutableMapOf<String, Any>()
        offset?.let { queryParams["offset"] = it }
        limit?.let { queryParams["limit"] = it }
        return givenBase()
            .baseUri(baseUri)
            .queryParams(queryParams)
            .get("/todos")
    }

    /**
     * Creates a new Todo.
     *
     * @param todo The [CreateTodoRequest] object containing the details of the Todo to be created.
     * @return The [Response] from the POST /todos endpoint.
     */
    fun createTodo(todo: CreateTodoRequest): Response {
        return givenBase()
            .baseUri(baseUri)
            .contentType(ContentType.JSON)
            .body(todo)
            .post("/todos")
    }

    /**
     * Updates an existing Todo identified by its ID.
     *
     * @param id The ID of the Todo to update.
     * @param todo The [CreateTodoRequest] object containing the updated data for the Todo.
     * @return The [Response] from the PUT /todos/{id} endpoint.
     */
    fun updateTodo(id: Long, todo: CreateTodoRequest): Response {
        return givenBase()
            .baseUri(baseUri)
            .contentType(ContentType.JSON)
            .body(todo)
            .put("/todos/$id")
    }

    /**
     * Deletes a Todo identified by its ID.
     *
     * @param id The ID of the Todo to delete.
     * @return The [Response] from the DELETE /todos/{id} endpoint.
     */
    fun deleteTodo(id: Long): Response {
        return authenticatedSpec
            .baseUri(baseUri)
            .delete("/todos/$id")
    }

    /**
     * Deletes a Todo identified by its ID without authentication.
     * This method is intended for negative testing scenarios where authentication is required.
     *
     * @param id The ID of the Todo to delete.
     * @return The [Response] from the DELETE /todos/{id} endpoint.
     */
    fun deleteTodoUnauthenticated(id: Long): Response {
        return givenBase()
            .baseUri(baseUri)
            .delete("/todos/$id")
    }

    /**
     * Retrieves a specific Todo by its ID.
     *
     * @param id The ID of the Todo to retrieve.
     * @return The [Response] from the GET /todos/{id} endpoint.
     */
    fun getTodoById(id: Long): Response {
        return givenBase()
            .baseUri(baseUri)
            .get("/todos/$id")
    }
}