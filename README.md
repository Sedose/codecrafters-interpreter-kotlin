[![progress-banner](https://backend.codecrafters.io/progress/interpreter/2af86dd6-03ef-4a23-8c5b-de136419a078)](https://app.codecrafters.io/users/codecrafters-bot?r=2qF)

Kotlin solution for 
["Build your own Interpreter" Challenge](https://app.codecrafters.io/courses/interpreter/overview).

This challenge follows the book
[Crafting Interpreters](https://craftinginterpreters.com/) by Robert Nystrom.

In this challenge I build an interpreter for
[Lox](https://craftinginterpreters.com/the-lox-language.html), a simple
scripting language. Along the way, I'll learn about tokenization, ASTs,
tree-walk interpreters and more.

Make sure you've read the "Welcome" part of the
book that contains these chapters:

- [Introduction](https://craftinginterpreters.com/introduction.html) (chapter 1)
- [A Map of the Territory](https://craftinginterpreters.com/a-map-of-the-territory.html)
  (chapter 2)
- [The Lox Language](https://craftinginterpreters.com/the-lox-language.html)
  (chapter 3)

**Note**: If you're viewing this repo on GitHub, head over to
[codecrafters.io](https://codecrafters.io) to try the challenge.

# Notes
- Make sure you set Maven to run ktlint:format before build
- ./mvnw clean verify - includes generating test coverage report as `target/site/jacoco/index.html`
- ./mvnw ktlint:format

# 🔥 Rules for my codebase
- 🚫 No Inheritance – Use composition instead of base classes.
- 🚫 No Static Functions – Use dependency injection (DI) to inject reusable components rather than relying on object or static functions.
- ✅ Favor Composition – Shared behavior should be extracted into injectable components, not inherited logic.
- ✅ Use Koin for DI – Every dependency should be injected, not hardcoded or globally accessed.
- ✅ Small, Focused Components – Keep responsibilities isolated to make testing and modification easier.
- 
