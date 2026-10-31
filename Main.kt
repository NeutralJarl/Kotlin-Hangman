// =====================================================================
// Kotlin Hangman
// A console game where the player guesses a hidden word one letter at
// a time. Each wrong guess costs a life and adds a piece to the gallows.
//
// Concepts demonstrated:
//   - Variables (val = immutable, var = mutable)
//   - Expressions, conditionals, loops, functions, classes
//   - Stretch challenge: collections (List, MutableSet) and `when`
//   - File reading (words.txt)
// =====================================================================

// java.io.File lets us read the word list from a text file.
// Kotlin can use any Java library, which is why we import from java.*
import java.io.File

// ---------------------------------------------------------------------
// TYPES
// An enum class is a fixed set of named values. Using enums instead of
// strings or numbers makes the code safer and easier to read.
// ---------------------------------------------------------------------

// The three states a game can be in.
enum class GameState { PLAYING, WON, LOST }

// The three possible outcomes of a single guess.
enum class GuessResult { CORRECT, WRONG, ALREADY_GUESSED }

// ---------------------------------------------------------------------
// GAME LOGIC
// HangmanGame holds everything about ONE round: the secret word, the
// letters guessed so far, and the lives remaining. Keeping the rules in
// a class means the input/output code below stays simple.
//
// The constructor takes two parameters:
//   - secret:   the word to guess. `private val` makes it a read-only
//               property that only this class can see (so nothing
//               outside can peek at the answer mid-game).
//   - maxLives: how many wrong guesses are allowed. It defaults to 6,
//               so HangmanGame("kotlin") works without passing it.
// ---------------------------------------------------------------------
class HangmanGame(private val secret: String, val maxLives: Int = 6) {

    // A MutableSet stores each letter at most once, which is perfect for
    // tracking guesses: guessing "a" twice can't create a duplicate.
    // `mutableSetOf<Char>()` creates an empty set that we can add to.
    private val guessed = mutableSetOf<Char>()

    // `var` means this value can change. It starts at maxLives and drops
    // by one for each wrong guess.
    // `private set` means code outside the class can READ lives but
    // cannot change it directly. Only guess() below can lower it.
    var lives = maxLives
        private set

    // A computed property: it has no stored value, and the code in `get()`
    // runs every time you read `game.state`.
    // `when` without an argument checks each condition from top to bottom
    // and uses the first one that is true (like an if / else-if chain,
    // but it returns a value).
    val state: GameState
        get() = when {
            // `all` is true only if EVERY letter of the secret has been guessed.
            secret.all { it in guessed } -> GameState.WON
            lives <= 0 -> GameState.LOST
            else -> GameState.PLAYING
        }

    // All wrong letters guessed so far, sorted alphabetically.
    // `filter` keeps only the guessed letters that are NOT in the secret.
    // `it` is the automatic name for the current item in a lambda.
    val wrongGuesses: List<Char>
        get() = guessed.filter { it !in secret }.sorted()

    // Builds the display string, e.g. "k _ t l _ n".
    // `map` goes through each character and produces a new list: the
    // letter itself if it was guessed, otherwise an underscore.
    // `joinToString(" ")` then glues the list together with spaces.
    // The `if` here is an expression: it produces a value.
    fun maskedWord(): String =
        secret.map { if (it in guessed) it else '_' }.joinToString(" ")

    // Processes one guess and reports what happened.
    fun guess(letter: Char): GuessResult {
        // `add` returns false if the letter was already in the set, so
        // this single line both records the guess and detects repeats.
        if (!guessed.add(letter)) return GuessResult.ALREADY_GUESSED

        // `letter in secret` checks whether the letter appears in the word.
        return if (letter in secret) {
            GuessResult.CORRECT
        } else {
            lives--                    // wrong guess: lose a life
            GuessResult.WRONG
        }
    }

    // Lets the outside code show the answer once the game is over.
    fun reveal(): String = secret
}

// ---------------------------------------------------------------------
// DISPLAY
// `stages` is an immutable List (val) of seven ASCII drawings. Index 0
// is the empty gallows (no mistakes), index 6 is the full figure (all
// six lives gone).
//
// Triple quotes """ make a raw string that can span several lines and
// keeps backslashes as-is. `trimIndent()` removes the leading spaces so
// the drawings print without extra indentation.
// ---------------------------------------------------------------------
val stages = listOf(
    """
      +---+
      |   |
          |
          |
          |
    =========
    """.trimIndent(),
    """
      +---+
      |   |
      O   |
          |
          |
    =========
    """.trimIndent(),
    """
      +---+
      |   |
      O   |
      |   |
          |
    =========
    """.trimIndent(),
    """
      +---+
      |   |
      O   |
     /|   |
          |
    =========
    """.trimIndent(),
    """
      +---+
      |   |
      O   |
     /|\  |
          |
    =========
    """.trimIndent(),
    """
      +---+
      |   |
      O   |
     /|\  |
     /    |
    =========
    """.trimIndent(),
    """
      +---+
      |   |
      O   |
     /|\  |
     / \  |
    =========
    """.trimIndent()
)

// Prints the right drawing. The number of mistakes made so far equals
// (maxLives - lives), and that number is the index into `stages`.
fun drawGallows(game: HangmanGame) {
    println(stages[game.maxLives - game.lives])
}

// ---------------------------------------------------------------------
// WORD LIST
// Reads words from a text file (one word per line). If the file is
// missing or has no usable words, a built-in list is used instead, so
// the game always works.
// The `path` parameter has a default value, so loadWords() is enough.
// ---------------------------------------------------------------------
fun loadWords(path: String = "words.txt"): List<String> {
    val file = File(path)

    // `if` used as an expression: whichever branch runs becomes the
    // value stored in `fromFile`.
    val fromFile = if (file.exists()) {
        file.readLines()                                        // read every line into a list
            .map { it.trim().lowercase() }                      // clean up spaces, make lowercase
            .filter { it.length >= 3 && it.all(Char::isLetter) } // keep real words only
    } else {
        emptyList()                                             // no file: nothing loaded
    }

    // `ifEmpty` returns the fallback list only when `fromFile` is empty.
    return fromFile.ifEmpty {
        listOf("kotlin", "android", "variable", "function", "compiler", "keyboard", "program")
    }
}

// ---------------------------------------------------------------------
// ONE ROUND OF THE GAME
// Handles all the input and output for a single word. The rules
// themselves live in HangmanGame; this function just talks to the player.
// ---------------------------------------------------------------------
fun playRound(word: String) {
    // Create a fresh game for this word. `val` because we never point
    // `game` at a different object (the object's internals still change).
    val game = HangmanGame(word)

    // Keep looping for as long as the game is still being played.
    while (game.state == GameState.PLAYING) {
        // Show the current situation.
        println()
        drawGallows(game)
        println("Word:  ${game.maskedWord()}")       // ${...} inserts a value into a string
        println("Lives: ${game.lives}    Wrong: ${game.wrongGuesses.joinToString(" ")}")
        print("Guess a letter: ")

        // Read what the player typed. The chain works like this:
        //   readlnOrNull() -> the typed line, or null if input has ended
        //   ?.trim()       -> remove spaces (the ?. skips this if null)
        //   ?.lowercase()  -> so "A" and "a" count as the same guess
        //   ?: return      -> if we got null, leave the function
        val input = readlnOrNull()?.trim()?.lowercase() ?: return

        // `singleOrNull()` gives the character if the text is exactly one
        // character long, otherwise null. The type is Char? (nullable).
        val letter = input.singleOrNull()

        // Validate: it must be a single character AND a letter.
        // `||` short-circuits, so isLetter() only runs when letter isn't
        // null. After this check Kotlin knows `letter` is a non-null Char.
        if (letter == null || !letter.isLetter()) {
            println("Please enter a single letter.")
            continue                                  // skip to the next loop iteration
        }

        // `when` with an argument compares the value against each case.
        // Here it picks a message based on what guess() returned.
        when (game.guess(letter)) {
            GuessResult.CORRECT -> println("Nice!")
            GuessResult.WRONG -> println("Nope.")
            GuessResult.ALREADY_GUESSED -> println("You already guessed '$letter'.")
        }
    }

    // The loop ended, so the game is over. Show the final drawing and result.
    println()
    drawGallows(game)

    // Another `when`. Kotlin requires every enum value to be covered here,
    // so PLAYING gets an empty branch even though it can't happen.
    when (game.state) {
        GameState.WON -> println("You won! The word was \"${game.reveal()}\".")
        GameState.LOST -> println("You lost. The word was \"${game.reveal()}\".")
        GameState.PLAYING -> {}
    }
}

// ---------------------------------------------------------------------
// ENTRY POINT
// Kotlin starts running here. This function loads the words once, then
// plays rounds until the player says they're done.
// ---------------------------------------------------------------------
fun main() {
    val words = loadWords()
    println("=== Kotlin Hangman ===")

    // A do-while loop always runs its body at least once, then repeats
    // as long as the condition at the bottom is true.
    do {
        // `random()` picks one word from the list at random.
        playRound(words.random())

        print("\nPlay again? (y/n): ")
        // This is a Boolean: true only if the player typed "y".
        // If input has ended (null), the comparison is false and we stop.
        val again = readlnOrNull()?.trim()?.lowercase() == "y"
    } while (again)

    println("Thanks for playing!")
}
