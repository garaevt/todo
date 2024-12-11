package tests

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTest {

    protected val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * Executed before all tests in the test container.
     */
    @BeforeAll
    fun setUpCommon() {
        logger.info("Test run started")
    }


    /**
     * Executed after all tests in the test container.
     */
    @AfterAll
    fun afterAllCommon() {
        logger.info("Test run completed")
    }
}