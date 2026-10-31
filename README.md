# Overview

As a software engineer, I want to be comfortable picking up new languages quickly and understanding what makes each one different. Kotlin is widely used for Android and general application development, and it is built to improve on Java while still working with the Java libraries. I wrote this project to learn how Kotlin handles the fundamentals (variables, conditionals, loops, functions, and classes) and to see how its syntax compares to languages I have already used.

The software is a console-based Hangman game. The program picks a secret word, shows it as a row of blanks, and lets the player guess one letter at a time. Correct guesses reveal the matching letters, and wrong guesses cost a life and add a piece to an ASCII gallows. The game ends when the word is completed or all six lives are gone, and the player can then choose to play again. Words are loaded from a `words.txt` file, with a built-in list as a fallback if the file is missing.

The game demonstrates the following parts of the Kotlin language:

- Immutable and mutable variables (`val` and `var`)
- Expressions, including `if` and `when` used as expressions that return values
- Conditionals and loops (`while`, `do-while`, `continue`)
- Functions with default parameters
- Classes with private properties, computed properties, and private setters
- Enum classes for game state and guess results
- Collections: `List` for the word list and drawings, and `MutableSet` for the guessed letters, along with `map`, `filter`, `all`, and `joinToString`
- Null safety with `?.`, `?:`, and nullable types
- File reading through the Java standard library

My purpose in writing this was to practice the core syntax in a program small enough to understand completely, but with enough structure (a class, enums, and separate functions) to show how Kotlin code is organized in practice.

[Software Demo Video](http://youtube.link.goes.here)

# Development Environment

I developed the software in Visual Studio Code with the Kotlin extension by fwcd for syntax highlighting and error checking. I compiled and ran the program from the terminal using the Kotlin command-line compiler (`kotlinc`), and I set up build and run tasks in `.vscode/tasks.json` so the program can be built with a keyboard shortcut.

The program is written in Kotlin and runs on the Java Virtual Machine, so it requires a Java Development Kit (JDK 17 or newer). It uses only the Kotlin standard library plus `java.io.File` from the Java standard library for reading the word list. No external libraries are needed.

To build and run:

```
kotlinc Main.kt -include-runtime -d hangman.jar
java -jar hangman.jar
```

Run the game from the project folder so that `words.txt` is found.

# Useful Websites

- [Kotlin Official Site](https://kotlinlang.org/)
- [Kotlin Tutorial - Programiz](https://www.programiz.com/kotlin-programming)
- [Kotlin Variable Types - Programiz](https://www.programiz.com/kotlin-programming/variable-types)
- [Kotlin Collections Overview](https://kotlinlang.org/docs/reference/collections-overview.html)
- [Kotlin Comparison to Java](https://kotlinlang.org/docs/reference/comparison-to-java.html)
- [Kotlin - Wikipedia](https://en.wikipedia.org/wiki/Kotlin_(programming_language))

# Future Work

- Add difficulty levels that change the number of lives or the word length
- Add word categories (for example, programming terms or animals) chosen at the start of a round
- Keep a score or win/loss record across rounds and save it to a file
- Show the full list of guessed letters, not just the wrong ones
- Add a hint option that costs a life
- Add unit tests for the `HangmanGame` class