[![progress-banner](https://backend.codecrafters.io/progress/interpreter/2af86dd6-03ef-4a23-8c5b-de136419a078)](https://app.codecrafters.io/users/codecrafters-bot?r=2qF)

# Kotlin Interpreter for Lox

This repository contains my Kotlin implementation of the ["Build your own Interpreter" Challenge](https://app.codecrafters.io/courses/interpreter/overview), inspired by [Crafting Interpreters](https://craftinginterpreters.com/) by Robert Nystrom.

## Overview
In this challenge, I'm building an interpreter for [Lox](https://craftinginterpreters.com/the-lox-language.html), a lightweight scripting language. Key concepts covered include:

- **Tokenization**
- **Abstract Syntax Trees (ASTs)**
- **Tree-walk Interpreters**

It's recommended to familiarize yourself with these chapters from the book:

- [Introduction](https://craftinginterpreters.com/introduction.html) (Chapter 1)
- [A Map of the Territory](https://craftinginterpreters.com/a-map-of-the-territory.html) (Chapter 2)
- [The Lox Language](https://craftinginterpreters.com/the-lox-language.html) (Chapter 3)

For the interactive challenge experience, visit [codecrafters.io](https://codecrafters.io).

## Getting Started

Useful commands:

```bash
# Format code according to ktlint guidelines
./mvnw ktlint:format

# Clean, verify, and generate a test coverage report
./mvnw clean verify

# Automatically update versions in pom.xml
./mvnw versions:use-latest-releases

./mvnw clean install
```

The test coverage report will be available at `target/site/jacoco/index.html`.

## Guidelines

- ✅ Prefer Dependency Injection using Koin.
- ✅ Keep the Codebase Auto-Formatted → Use Ktlint linter.
- ✅ Commit messages according to https://www.conventionalcommits.org/en/v1.0.0/.

## Notes
- No GitHub actions are used in this repository cause CodeCrafters provides a CI/CD pipeline. \
And adding GitHub actions will cause the build to fail cause it would interfere with the CodeCrafters pipeline.

## Example prompt for agents like VS Code Copilot Agent Mode

```
Implement support for parsing multiplicative operators (`*` and `/`) in the interpreter. This includes the following tasks:

1. **Code Implementation**:
   - Update the `Expr` class if necessary to support multiplicative expressions.
   - Modify the `Parser` class to handle the parsing of multiplication (`*`) and division (`/`) operators as described in Section 6.2: Recursive Descent Parsing.

2. **Unit Tests**:
   - Add unit tests in `ParserTest.kt` to verify the correct parsing of multiplicative operators (`*` and `/`).
   - Include test cases for:
     - Simple multiplication (e.g., `16 * 38`).
     - Simple division (e.g., `38 / 58`).
     - Combined multiplication and division (e.g., `16 * 38 / 58`).

3. **Integration Tests**:
   - Add integration test cases in `ParserInProcessIT.kt` to ensure the program processes multiplicative operators and outputs the correct Abstract Syntax Tree (AST).
   - Create the following test files in `src/integration-test/resources/`:
     - `multiplication_test.lox` with the content `16 * 38`.
     - `division_test.lox` with the content `38 / 58`.
     - `combined_multiplication_division_test.lox` with the content `16 * 38 / 58`.

4. **Validation**:
   - Run all unit and integration tests to ensure everything works as expected.
   - Ensure the output format matches the specification in the book's repository (e.g., `(/ (* 16.0 38.0) 58.0)`).

5. **Git**:
   - Stage the newly created test files (`multiplication_test.lox`, `division_test.lox`, and `combined_multiplication_division_test.lox`) in git.

Notes:
- Use ./mvnw, not mvn in terminal
```
