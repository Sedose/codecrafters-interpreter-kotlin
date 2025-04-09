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
