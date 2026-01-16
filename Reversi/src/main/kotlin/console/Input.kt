package console

/**
 * Represents a command input consisting of a command string and its associated arguments.
 *
 * @param cmdStr The command string input, representing the core action to be executed.
 * @param args A list of strings representing the arguments associated with the command.
 */
data class LineCommand(val cmdStr: String, val args: List<String>)

/**
 * Reads a user input from the console, processes the input to extract a command and its arguments,
 * and returns a corresponding [LineCommand] object. If the input is invalid or empty returns null.
 *
 * @return A [LineCommand] object if the input is valid, or null if the input is empty or invalid.
 */
fun readCommandOrNull(): LineCommand?{
    val input = readlnOrNull()?.lowercase()?.trim()
    if (input == null || input.isBlank()) return null
    val parts = input.split(" ")
    return LineCommand(parts[0], parts.drop(1))
}
