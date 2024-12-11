package services

import enums.HttpStatusCode
import exceptions.ApiException
import io.restassured.response.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import utils.ResponseValidator

/**
 * Base service class that provides common functionalities for API services.
 *
 * This class includes methods for validating API responses based on expected
 * status codes and content types. It serves as a foundation for other service
 * classes to inherit from, promoting code reuse and consistency across the
 * application.
 */
open class BaseService {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * Validates the [Response] status code against the expected [HttpStatusCode].
     *
     * This method delegates the validation to [ResponseValidator]. If the actual
     * status code does not match the expected one, an [ApiException] is thrown.
     *
     * @param response The [Response] to validate.
     * @param expectedStatusCode The expected [HttpStatusCode].
     * @throws ApiException if the status code does not match the expectation.
     */
    protected fun validateResponse(response: Response, expectedStatusCode: HttpStatusCode) {
        try {
            ResponseValidator.validateStatusCode(response, expectedStatusCode)
            logger.debug("Response status code ${response.statusCode} matches expected ${expectedStatusCode.code}.")
        } catch (e: ApiException) {
            logger.error("Response validation failed: ${e.message}")
            throw e
        }
    }

    /**
     * Validates the [Response] status code and Content-Type against the expected values.
     *
     * This method delegates the validation to [ResponseValidator]. If either the actual
     * status code or the Content-Type does not match the expected values, an [ApiException] is thrown.
     *
     * @param response The [Response] to validate.
     * @param expectedStatusCode The expected [HttpStatusCode].
     * @param expectedContentType The expected Content-Type as a [String].
     * @throws ApiException if either the status code or Content-Type does not match the expectation.
     */
    protected fun validateResponse(
        response: Response,
        expectedStatusCode: HttpStatusCode,
        expectedContentType: String
    ) {
        try {
            ResponseValidator.validateStatusCode(response, expectedStatusCode)
            ResponseValidator.validateContentType(response, expectedContentType)
            logger.debug("Response status code ${response.statusCode} and Content-Type $expectedContentType match expected values.")
        } catch (e: ApiException) {
            logger.error("Response validation failed: ${e.message}")
            throw e
        }
    }
}