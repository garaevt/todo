# Project Overview

This repository contains automated tests for a TODO manager application. The application provides CRUD operations for
managing TODO items and also supports WebSocket updates for real-time notifications.

The test framework is built with:

* **Kotlin** for the programming language.
* **JUnit5** for structured test execution.
* **REST-assured** for HTTP requests and responses.
* **AssertJ** for fluent assertions.
* **Allure** for generating comprehensive test reports.
* **OkHttp** for WebSocket testing.

## Key Features

* **CRUD Endpoints**: Create, retrieve, update, and delete TODO items.
* **Pagination**: Support for `offset` and `limit` parameters in the GET `/todos` endpoint.
* **WebSockets**: Real-time notifications for created, updated, and deleted TODO items.

## Test Coverage and Negative Scenarios

Current test coverage focuses primarily on core functionalities and positive paths. While some negative scenarios are included (e.g., invalid inputs, missing fields), many potential negative test cases are deliberately not present here. Such negative or adversarial testing is intended to be performed separately as part of dedicated fuzz testing or specialized security and robustness testing suites. This helps maintain clarity and scope within the main test suite.

## Environment Profiles

The tests can be run against different environments. By default, the `local` environment is used, but you can specify
others, such as `qa`, `dev`, or `prod`.

The configuration files are located in:
`src/main/resources/application-*.properties`

* `application-local.properties`
* `application-qa.properties`
* `application-dev.properties`
* `application-prod.properties`

Switching environments is done by setting the system property `-Denv=<environment>`. For example:

```
./gradlew test -Denv=qa
```

This will load `application-qa.properties`.

## Requirements

* Docker to run the provided TODO application image.
* Java 19 (managed by Gradle toolchain, no manual setup required if you have a compatible JDK installed).
* Gradle (the project includes a Gradle wrapper, so no global installation is necessary).

## Running the Application

1. Load the Docker image for the TODO application:
   ```
   docker load -i todo_image.tar
   ```

2. Run the application:
   ```
   docker run -p 8080:4242 todo_image
   ```

3. Access the application:
    * REST Endpoints: `http://localhost:8080`
    * WebSocket Endpoint: `ws://localhost:8080/ws`

For more detailed logs, you can enable verbose mode:

```
docker run -e VERBOSE=1 -p 8080:4242 todo_image
```

## Building the Tests

The project uses Gradle as the build tool. All dependencies are declared in `build.gradle.kts`. You do not need to
install Gradle globally; the included wrapper `./gradlew` handles it.

To build without running tests:

```
./gradlew build
```

## Running the Tests

To run all tests against the default `local` environment:

```
./gradlew test
```

To run tests against a specific environment (e.g., `qa`):

```
./gradlew testQA
```

or

```
./gradlew test -Denv=qa
```

You can also define custom tasks in Gradle (e.g., `testDev`, `testProd`) which set the environment internally.

## Test Structure

* `src/test/kotlin/tests/`: Contains test classes grouped by functionality (CreateTodoTests, GetTodosTests, etc.).
* `src/test/kotlin/steps/`: Contains step classes (`TodoSteps`, `TodoWebSocketSteps`) that provide higher-level actions
  composed of multiple service calls.
* `src/main/kotlin/api/` and `src/main/kotlin/services/`: API client classes and service classes abstracting business
  logic.
* `src/main/kotlin/models/`: Data models for requests and responses.
* `src/main/kotlin/utils/`: Utility classes for response validation, string generation, etc.

## Generating Allure Reports

Allure is integrated to provide rich test reports.

Steps:

1. Run the tests (e.g., `./gradlew test`).
2. Generate the Allure report:
   ```
   ./gradlew allureReport
   ```
   This collects Allure results from the test run.

3. Serve the Allure report locally:
   ```
   ./gradlew allureServe
   ```
   A local server will start, and you can view the report in your browser (URL will be provided in the console).

Allure reports provide:

* Test execution summaries.
* Individual test details, including logs and attachments.
* Categories for failures to quickly identify common issues.
* Historical trends if you run tests repeatedly and keep results.

## Additional Tips

* Use `-Denv=<env>` to switch environments dynamically without changing code.
* Configure logging levels in `log4j2.xml` if you need more or less verbosity.
* Parallel test execution is enabled to speed up test runs, you can customize the number of parallel threads in
  the `tasks.test` Gradle configuration.
* Consider using Docker Compose to manage both the TODO application and any mock dependencies in a single environment
  file (not included by default).

## License

This project is provided as-is for testing and demonstration purposes. For more information or inquiries, please
contact:  
**Timur Garaev**  
**Email**: gartim1992@gmail.com