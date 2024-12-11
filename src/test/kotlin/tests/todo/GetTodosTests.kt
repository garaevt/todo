package tests.todo

import constants.ResourceLocks
import enums.PageSize
import models.todo.CreateTodoRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import steps.todo.TodoSteps
import tests.BaseTest
import utils.StringUtils.generateRandomString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTodosTests : BaseTest() {

    private val todoSteps = TodoSteps()
    private val createdTodoIds: MutableList<Long> = mutableListOf()

    @BeforeAll
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun beforeAll() {
        for (i in 1..51) {
            val todo = todoSteps.createTodo(
                CreateTodoRequest(
                    text = generateRandomString(),
                    completed = false
                )
            )
            todo?.let { createdTodoIds.add(it.id) }
        }
    }

    @AfterAll
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun afterAll() {
        createdTodoIds.forEach { todoSteps.deleteTodo(it) }
    }

    /**
     * Test Case 1: Retrieve all Todos without pagination parameters.
     *
     * This test verifies that fetching all Todos without specifying pagination parameters
     * returns the correct number of Todos. Given that 51 Todos were created in [beforeAll],
     * and assuming an existing Todo, the total should be 52.
     *
     * Steps:
     * 1. Invoke the [TodoSteps.getTodos] method without any pagination parameters.
     * 2. Assert that the returned list of Todos is not null and not empty.
     * 3. Assert that the size of the returned list is equal to 52.
     */
    @Test
    @DisplayName("GET /todos - Retrieve all Todos without pagination")
    fun getAllTodosTest() {
        val todos = todoSteps.getTodos()

        assertThat(todos)
            .isNotNull
            .isNotEmpty

        assertThat(todos.size)
            .isEqualTo(52)
    }

    /**
     * Test Case 2: Retrieve Todos with various limit parameters.
     *
     * This parameterized test verifies that fetching Todos with different `limit` values
     * returns the expected number of Todos. It uses the [PageSize] enum to supply different
     * limit values for each test iteration.
     *
     * Steps:
     * 1. Invoke the [TodoSteps.getTodos] method with the specified `limit` parameter.
     * 2. Assert that the returned list of Todos is not null.
     * 3. Assert that the size of the returned list is equal to the specified `limit`.
     *
     * @param pageSize The [PageSize] enum value providing different limit sizes.
     */
    @ParameterizedTest(name = "Limit = {0}")
    @EnumSource(PageSize::class)
    @DisplayName("GET /todos - Verify 'limit' parameter")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun getTodosWithLimitTest(pageSize: PageSize) {
        val todos = todoSteps.getTodos(limit = pageSize.size)

        assertThat(todos)
            .isNotNull

        assertThat(todos.size)
            .isEqualTo(pageSize.size)
    }

    /**
     * Test Case 3: Retrieve Todos with an offset parameter.
     *
     * This test verifies that fetching Todos with an `offset` parameter correctly skips the specified
     * number of Todos. It ensures that the Todos returned after applying the offset do not include
     * the first Todo in the list.
     *
     * Steps:
     * 1. Invoke the [TodoSteps.getTodos] method without the `offset` parameter to retrieve all Todos.
     * 2. Invoke the [TodoSteps.getTodos] method with an `offset` of 10.
     * 3. Assert that the list of Todos retrieved with the offset is not null and not empty.
     * 4. Assert that the first Todo in the list without offset is not equal to the first Todo in the list with offset.
     */
    @Test
    @DisplayName("GET /todos - Verify 'offset' parameter")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun getTodosWithOffsetTest() {
        val todosWithoutOffset = todoSteps.getTodos()
        val todosWithOffset = todoSteps.getTodos(offset = 10)

        assertThat(todosWithOffset)
            .isNotNull
            .isNotEmpty

        assertThat(todosWithoutOffset.first().id)
            .isNotEqualTo(todosWithOffset.first().id)
    }

    /**
     * Test Case 4: Retrieve Todos with both limit and offset parameters.
     *
     * This test verifies that fetching Todos with both `limit` and `offset` parameters
     * returns the correct subset of Todos. It ensures that the number of Todos returned
     * matches the specified `limit` and that the offset correctly skips the initial Todos.
     *
     * Steps:
     * 1. Define `limit` as 10 and `offset` as 20.
     * 2. Invoke the [TodoSteps.getTodos] method with both `limit` and `offset` parameters.
     * 3. Assert that the returned list of Todos is not null.
     * 4. Assert that the size of the returned list is equal to the specified `limit`.
     */
    @Test
    @DisplayName("GET /todos - Verify 'limit' and 'offset' parameters simultaneously")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun getTodosWithLimitAndOffsetTest() {
        val limit = PageSize._10.size
        val offset = 20
        val todos = todoSteps.getTodos(limit = limit, offset = offset)

        assertThat(todos)
            .isNotNull

        assertThat(todos.size)
            .isEqualTo(limit)
    }

    /**
     * Test Case 5: Retrieve Todos with an offset exceeding the total number of Todos.
     *
     * This test verifies that fetching Todos with an `offset` parameter that exceeds
     * the total number of available Todos returns an empty list. It ensures that the API
     * correctly handles cases where the offset is out of bounds.
     *
     * Steps:
     * 1. Determine the total number of Todos by invoking [TodoSteps.getAllTodos].
     * 2. Invoke the [TodoSteps.getTodos] method with an `offset` equal to the total number of Todos plus one.
     * 3. Assert that the returned list of Todos is empty.
     */
    @Test
    @DisplayName("GET /todos - Verify with offset exceeding the total number of records")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun getTodosWithInvalidParametersTest() {
        val allTodosSize = todoSteps.getAllTodos().size
        val todos = todoSteps.getTodos(offset = allTodosSize + 1)

        assertThat(todos)
            .isEmpty()
    }

    // TODO: Test Case 6: Verify retrieving Todos with a limit of zero
    // TODO: Test Case 7: Verify sorting of Todos (if supported)
    // TODO: Test Case 8: Verify filtering of Todos by completed status (if supported)
    // TODO: Test Case 9: Verify presence of necessary fields in the response
    // TODO: Test Case 10: Verify performance under a high number of requests
}