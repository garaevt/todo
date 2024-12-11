package utils

import kotlin.random.Random

/**
 * Utility object for String-related operations.
 */
object StringUtils {

    /**
     * Generates a random string of the specified length.
     *
     * @param length The length of the random string to generate.
     * @return A randomly generated string consisting of uppercase letters, lowercase letters, and digits.
     */
    fun generateRandomString(length: Int = 10): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars[Random.nextInt(allowedChars.length)] }
            .joinToString("")
    }
}