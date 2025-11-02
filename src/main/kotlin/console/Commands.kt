package console

import model.Clash
import model.Game
import model.GameStorage
import model.Name
import model.PiecesColor.Companion.toPieceColorOrNull
import model.Reversi
import model.refresh
import model.toCoordinateOrNull
import kotlin.system.exitProcess

/**
 * Represents a command that can be executed by the user interface.
 */
class Command(
    val commandHelpMsg: String,
    val execute: (context: CommandContext,args: List<String>) -> CommandContext = {c, _ -> c },
)

/**
 * Returns a map containing all available commands in the game.
 *
 * @param str The instance of GameStorage required for initializing certain commands.
 * @return A map where the keys are command names (as strings) and the values are corresponding Command.
 */
fun getAllCommands(str: GameStorage): Map<String, Command> = mapOf(
    "play" to Play,
    "new" to new(str),
    "help" to help(str),
    "exit" to Exit,
    "show" to Show,
    "pass" to Pass,
    "targets" to Targets,
    "join" to join(str),
    "refresh" to Refresh
)

/**
 * Converts the string representation of a command into a corresponding [Command] object
 * using the provided `GameStorage` instance.
 *
 * @param str A storage.
 * @return The [Command] object corresponding to the string, if it exists.
 * @throws CommandException.Unknown If the string does not correspond to a valid command.
 */
fun String.toCommand(str: GameStorage): Command {
    val commands = getAllCommands(str)
    return commands[this] ?: throw CommandException.Unknown("Invalid Command")
}

/**
 * Represents the "exit" command which terminates the application.
 */
private val Exit = Command(commandHelpMsg = "exit - Exits the application") { _, _ ->
    exitProcess(0)
}

/**
 * Executes the "help" command, which displays a list of all available commands
 * along with their respective descriptions.
 *
 * @param storage The instance of `GameStorage`, used to retrieve all available commands.
 */
private fun help(storage: GameStorage) = Command(commandHelpMsg = "help - Shows the command list") { context, _ ->
    println()
    getAllCommands(storage)
        .toSortedMap()
        .forEach { (_, cmd) -> println("- ${cmd.commandHelpMsg}") }
    println()
    context
}

/**
 * Creates a new command to start a game. It initializes either a local or distributed game
 * based on the provided arguments and game storage.
 *
 * @param storage The storage used to manage game data.
 * @return A [Command] instance that, when executed, starts a new game.
 * @throws CommandException.InvalidParameters If the arguments provided are invalid.
 */
private fun new(storage: GameStorage): Command = Command(
    "new <#|@> [name] - Starts a new game"
) { _, args ->
    if (args.isEmpty() || args.size > 2) throw CommandException.InvalidParameters(new(storage))
    val firstPlayerColor = args[0].toPieceColorOrNull()
        ?: throw CommandException.InvalidParameters(new(storage))
    val gameNameStr = args.getOrNull(1)

    val game: Game

    if (gameNameStr != null) {
        println("Starting distributed game...")
        game = Clash.start(Name(gameNameStr), storage, firstPlayerColor)
    } else {
        println("Starting local game...")
        game = Reversi(firstPlayerColor)
    }

    val newContext = CommandContext.GameInProgress(game, false)
    newContext.also { it.show() }
}

/**
 * Creates a command for joining an existing distributed game.
 *
 * @param storage The game storage used to locate and join the specified game.
 * @return A [Command] instance for executing the join operation.
 */
private fun join(storage: GameStorage): Command = Command(
    "join <name> - Joins an existing distributed game"
) { _, args ->
    if (args.size != 1) throw CommandException.InvalidParameters(join(storage), "Usage: join <name>")

    val gameName = Name(args[0])

    val game= Clash.join(gameName, storage)

    val newContext = CommandContext.GameInProgress(game, false)

    newContext.also { it.show() }
}

/**
 * Represents the "play" command, which allows a user to play a move during an ongoing game.
 *
 * @throws CommandException.IllegalContext if the command is executed outside a game in progress.
 * @throws CommandException.InvalidParameters if the provided position is invalid or the move cannot be played.
 */
private val Play: Command = Command("play <position> - Plays a move") { context, args ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    val coordinate = args.firstOrNull()?.toCoordinateOrNull()
        ?: throw CommandException.InvalidParameters(Play, "Invalid coordinate\n")

    val newContext = context.play(coordinate) ?: throw CommandException.InvalidParameters(Play, "Invalid move\n")
    newContext.also { it.show() }
}

/**
 * Represents a command that allows the current player to pass their turn during an ongoing game.
 * @throws CommandException.IllegalContext if the command is attempted without a game in progress or
 *         when the turn cannot be passed due to available moves.
 */
private val Pass: Command = Command("pass - Passes the turn") { context, _ ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    val newContext = context.pass()
        ?: throw CommandException.IllegalContext("Can't pass, moves are still available")

    newContext.also { it.show() }
}

/**
 * Represents the "refresh" command in the game, used to update the current game state
 * in distributed games by reloading its data from storage.
 * @throws CommandException.IllegalContext if the command is attempted without a game in progress.
 */
private val Refresh: Command = Command("refresh - Updates game state (in distributed games)") { context, _ ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    if (context.game is Clash) {
        val newClash = context.game.refresh()
        val newContext = context.copy(game = newClash)
        newContext.also { it.show() }

    } else {
        println("Refresh is only available in distributed games.")
        context.also { it.show() }
    }
}

/**
 * Represents a command used to display the current state of the game board.
 * @throws CommandException.IllegalContext If the command is executed outside a game context.
 */
private val Show: Command = Command("show - Displays the board") { context, _ ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    context.also { it.show() }
}

/**
 * Represents a command that toggles the display of move hints in the ongoing game.
 * @throws CommandException.IllegalContext If executed outside an ongoing game context.
 * @throws CommandException.InvalidParameters If more than one argument is passed or if the argument is invalid.
 */
private val Targets: Command = Command("targets [ON|OFF] - Toggles move hints") { context, args ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    val showTargets = when {
        args.size > 1 -> throw CommandException.InvalidParameters(Targets)
        args.isEmpty() -> {
            println(if (context.showTargets) "Targets are ON" else "Targets are OFF")
            return@Command context
        }
        args.first().uppercase() == "ON" -> true
        args.first().uppercase() == "OFF" -> false
        else -> throw CommandException.InvalidParameters(Targets)
    }

    val newContext = context.copy(showTargets = showTargets)
    newContext.also { it.show() }
}