package api

import config.Config
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification

/**
 * Base API class that provides common configurations and specifications
 * for making HTTP requests using RestAssured.
 *
 * This class sets up the base specifications, including timeouts and logging,
 * which can be utilized by derived API classes to perform HTTP operations.
 */
open class BaseApi {

    /**
     * The base [RequestSpecification] that includes common configurations
     * such as connection and socket timeouts.
     *
     * Initialized lazily to ensure that the configuration is set up
     * before any requests are made.
     */
    protected val baseSpec: RequestSpecification by lazy {
        RequestSpecBuilder()
            .setConfig(
                RestAssured.config().httpClient(
                    RestAssured.config().httpClientConfig
                        .setParam("http.connection.timeout", Config.TIMEOUT_SECONDS * 1000)
                        .setParam("http.socket.timeout", Config.TIMEOUT_SECONDS * 1000)
                )
            )
            .build()
    }

    init {
        RestAssured.filters(
            RequestLoggingFilter(LogDetail.ALL),
            ResponseLoggingFilter(LogDetail.ALL)
        )
    }

    /**
     * Provides an unauthenticated [RequestSpecification] for API requests.
     *
     * This specification includes the base configurations but does not include
     * any authentication headers. It is useful for endpoints that do not require
     * authentication.
     *
     * @return A [RequestSpecification] with base configurations applied.
     */
    protected fun givenBase(): RequestSpecification {
        return RestAssured.given().spec(baseSpec)
    }
}