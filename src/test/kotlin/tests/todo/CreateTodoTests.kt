package tests.todo

import constants.ResourceLocks
import enums.HttpStatusCode
import models.todo.CreateTodoRequest
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import steps.todo.TodoSteps
import utils.StringUtils.generateRandomString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import tests.BaseTest
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateTodoTests : BaseTest() {

    private val todoSteps = TodoSteps()
    private val createdTodoIds: MutableList<Long> = mutableListOf()

    @AfterAll
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun afterAll() {
        createdTodoIds.forEach { todoSteps.deleteTodo(it) }
    }

    /**
     * Test Case 1: Successfully create a Todo with valid data.
     *
     * This test verifies that a Todo can be created successfully when provided with valid input data.
     * It asserts that the response contains a non-null Todo object with the expected properties.
     *
     * Steps:
     * 1. Generate a valid [CreateTodoRequest] with random text and a `completed` status set to `false`.
     * 2. Invoke the [TodoSteps.createTodo] method to create the Todo.
     * 3. Assert that the returned Todo is not null.
     * 4. Add the created Todo's ID to the [createdTodoIds] list for cleanup.
     * 5. Perform soft assertions to verify that the Todo's `id`, `text`, and `completed` fields match the input.
     */
    @Test
    @DisplayName("POST /todos - Successfully create a Todo with valid data")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun createTodoWithValidDataTest() {
        val createTodoRequest = CreateTodoRequest(
            text = generateRandomString(10),
            completed = false
        )

        val todo = todoSteps.createTodo(
            todo = createTodoRequest
        )

        assertThat(todo)
            .isNotNull

        createdTodoIds.add(todo!!.id)

        assertSoftly {
            it.assertThat(todo.id)
                .isGreaterThan(0)
            it.assertThat(todo.text)
                .isEqualTo(createTodoRequest.text)
            it.assertThat(todo.completed)
                .isEqualTo(createTodoRequest.completed)
        }
    }

    /**
     * Parameterized Test: Negative Test Cases for Todo Creation.
     *
     * This parameterized test runs multiple scenarios where creating a Todo is expected to fail
     * due to invalid input data. Each test case provides a description, an invalid [CreateTodoRequest],
     * and the expected [HttpStatusCode] response.
     *
     * The test asserts that the API responds with the appropriate error status code and does not create a Todo.
     *
     * @param description A brief description of the test case.
     * @param createTodoRequest The [CreateTodoRequest] object containing invalid data.
     * @param expectedStatusCode The expected [HttpStatusCode] of the response.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidCreateTodoRequests")
    @DisplayName("POST /todos - Negative Test Cases")
    @ResourceLock(ResourceLocks.TODO_LOCK)
    fun createTodoNegativeTests(
        description: String,
        createTodoRequest: CreateTodoRequest,
        expectedStatusCode: HttpStatusCode,
    ) {

        val todo = todoSteps.createTodo(
            todo = createTodoRequest,
            expectedStatusCode = expectedStatusCode
        )

        assertThat(todo)
            .describedAs("Expected an $expectedStatusCode code for test case: $description")
            .isNull()
    }

    /**
     * Provides a stream of invalid [CreateTodoRequest] objects along with their corresponding test case descriptions
     * and expected [HttpStatusCode] responses.
     *
     * This method serves as a data provider for the [createTodoNegativeTests] parameterized test.
     *
     * @return A [Stream] of [Arguments] representing different negative test scenarios.
     */
    private fun provideInvalidCreateTodoRequests(): Stream<Arguments> = Stream.of(
        // Test Case 2: Fail to create a Todo when 'text' field is missing.
        Arguments.of(
            "Missing 'text' field",
            CreateTodoRequest(
                text = "",
                completed = false
            ),
            HttpStatusCode.BAD_REQUEST
        ),
        // Test Case 3: Fail to create a Todo when 'text' field is empty.
        Arguments.of(
            "Empty 'text' field",
            CreateTodoRequest(
                text = "   ",
                completed = false
            ),
            HttpStatusCode.BAD_REQUEST
        ),
        // Test Case 4: Fail to create a Todo when 'text' field exceeds maximum allowed length.
        Arguments.of(
            "Text field exceeds maximum length",
            CreateTodoRequest(
                text = generateRandomString(256),
                completed = false
            ),
            HttpStatusCode.BAD_REQUEST
        ),
        // Test Case 5: Fail to create a Todo when 'completed' field is missing (if nullable)
        Arguments.of(
            "Missing 'completed' field",
            CreateTodoRequest(
                text = generateRandomString(10)
                // Assuming 'completed' is nullable; otherwise, this test case may be irrelevant
            ),
            HttpStatusCode.BAD_REQUEST
        )
    )

    // TODO: Test Case 6: Fail to create a Todo with invalid data types for fields

    // TODO: Test Case 7: Fail to create a Todo when unauthorized (if API requires authentication)

    // TODO: Test Case 8: Successfully create multiple Todos in succession

    // TODO: Test Case 9: Fail to create a Todo with duplicate 'text' (if uniqueness is enforced)

    // TODO: Test Case 10: Verify response headers and status codes upon successful creation
}