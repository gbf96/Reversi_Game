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


fun String.toCommand(str: GameStorage): Command {
    val commands = getAllCommands(str)
    return commands[this] ?: throw CommandException.Unknown("Invalid Command")
}

private val Exit = Command(commandHelpMsg = "exit - Exits the application") { _, _ ->
    exitProcess(0)
}

private fun help(storage: GameStorage) = Command(commandHelpMsg = "help - Shows the command list") { context, _ ->
    println()
    getAllCommands(storage)
        .toSortedMap()
        .forEach { (_, cmd) -> println("- ${cmd.commandHelpMsg}") }
    println()
    context
}

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

private fun join(storage: GameStorage): Command = Command(
    "join <name> - Joins an existing distributed game"
) { _, args ->
    if (args.size != 1) throw CommandException.InvalidParameters(join(storage), "Usage: join <name>")

    val gameName = Name(args[0])

    val game= Clash.join(gameName, storage)

    val newContext = CommandContext.GameInProgress(game, false)

    newContext.also { it.show() }
}


private val Play: Command = Command("play <position> - Plays a move") { context, args ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    val coordinate = args.firstOrNull()?.toCoordinateOrNull()
        ?: throw CommandException.InvalidParameters(Play, "Invalid coordinate\n")

    val newContext = context.play(coordinate) ?: throw CommandException.InvalidParameters(Play, "Invalid move\n")
    newContext.also { it.show() }
}

private val Pass: Command = Command("pass - Passes the turn") { context, _ ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    val newContext = context.pass()
        ?: throw CommandException.IllegalContext("Can't pass, moves are still available")

    newContext.also { it.show() }
}

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

private val Show: Command = Command("show - Displays the board") { context, _ ->
    if (context !is CommandContext.GameInProgress) throw CommandException.IllegalContext("No game in progress")

    context.also { it.show() }
}

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