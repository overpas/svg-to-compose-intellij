# Testing

Arrange-Act-Assert pattern. newlines between the blocks in unit tests. Use /testing-on-the-toilet
skill.

## Running tests

```shell
./gradlew test # all tests
./gradlew test --tests <TestClass> # one test class, fastest feedback
```

Prefer a single test class for rapid feedback; run `test` before pushing.

## Test method naming

Use `` `backticked names with spaces` ``.

## No helper functions in tests

Never define helper functions inside a test class — no builders/factories with default arguments, no
shared setup functions. A test case must be readable top to bottom without jumping to another
function to learn what the data actually is. Verbosity and duplication are the accepted cost; do not
"clean it up" into helpers later.

## Test data

Never create test data with the API under test: the test then checks the code against itself.

## Where tests are not needed

- Simple classes that serve the purpose of a "Factory", which simply redirects to other creator
  class or function.
- Top level and `object` variables and constants
- Classes that fulfill the "helper" role for testing purposes.
