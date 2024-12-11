package utils

import enums.HttpStatusCode
import exceptions.ApiException
import io.restassured.response.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Utility object for validating API responses.
 *
 * Provides methods to validate the status code and Content-Type of HTTP responses.
 * Throws [ApiException] when validations fail, facilitating consistent error handling
 * across the application.
 */
object ResponseValidator {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * Validates that the response status code matches the expected [HttpStatusCode].
     *
     * @param response The [Response] to validate.
     * @param expectedStatusCode The expected [HttpStatusCode].
     * @throws ApiException if the status code does not match the expectation.
     */
    fun validateStatusCode(response: Response, expectedStatusCode: HttpStatusCode) {
        logger.info("Validating status code: expected ${expectedStatusCode.code}, actual ${response.statusCode}")
        if (response.statusCode != expectedStatusCode.code) {
            logger.error("Status code validation failed. Response: ${response.asString()}")
            throw ApiException(
                "Expected status code ${expectedStatusCode.code} but got ${response.statusCode}. " +
                        "Response: ${response.asString()}"
            )
        }
    }

    /**
     * Validates that the response Content-Type matches the expected value.
     *
     * @param response The [Response] to validate.
     * @param expectedContentType The expected Content-Type as a [String].
     * @throws ApiException if the Content-Type does not match the expectation.
     */
    fun validateContentType(response: Response, expectedContentType: String) {
        logger.info("Validating Content-Type: expected $expectedContentType, actual ${response.contentType}")
        if (response.contentType != expectedContentType) {
            logger.error("Content-Type validation failed. Response: ${response.asString()}")
            throw ApiException(
                "Expected Content-Type $expectedContentType but got ${response.contentType}. " +
                        "Response: ${response.asString()}"
            )
        }
    }
}