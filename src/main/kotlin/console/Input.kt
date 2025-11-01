package console

data class LineCommand(val cmdStr: String, val args: List<String>)

fun readCommandOrNull(): LineCommand?{
    val input = readlnOrNull()?.lowercase()?.trim()
    if (input == null || input.isBlank()) return null
    val parts = input.split(" ")
    return LineCommand(parts[0], parts.drop(1))
}
