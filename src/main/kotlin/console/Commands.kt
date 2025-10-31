package console

import model.Clash
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
    "refresh" to refresh
)


fun String.toCommand(str: GameStorage): Command {
    val commands = getAllCommands(str)
    return commands[this] ?: throw CommandException.Unknown("Invalid Command")
}

private fun join(storage: GameStorage): Command = Command(
    "join <name> - Joins an existing distributed game as the opponent."
) { _, args ->

    if (args.size != 1) {
        throw CommandException.InvalidParameters(join(storage))
    }

    val gameNameStr = args[0]
    val gameName = Name(gameNameStr)

    val clashRun = Clash.join(gameName, storage)

    val newContext =  CommandContext.DistributedGame(clashRun, false)
    newContext.also { newContext.show() }
}

private val refresh = Command("refresh - Updates the game state (in distributed games)."){
    context, _ ->
    if (context is CommandContext.DistributedGame){
        val newContext = context.copy(clash = context.clash.refresh())
        newContext.also { newContext.show() }
    }else{
        context
    }
}

private val Exit = Command(commandHelpMsg = "exit - Exits the application")
{_, _ -> exitProcess(0) }


private fun new(storage: GameStorage): Command = Command("new [#|@] <name> - Starts a new game") { _, args ->
    if (args.isEmpty() || args.size > 2) throw CommandException.InvalidParameters(new(storage))

    val firstPlayerColor = args[0].toPieceColorOrNull()
        ?: throw CommandException.InvalidParameters(new(storage), "Invalid color")
    val gameNameStr = args.getOrNull(1)

    if (gameNameStr != null) {
        val clashRun = Clash.start(Name(gameNameStr), storage, firstPlayerColor)
        val newContext = CommandContext.DistributedGame(clashRun, false)
        newContext.also { newContext.show() }

    } else {
        val reversi = Reversi(firstPlayerColor)
        val newContext = CommandContext.LocalGame(reversi, false)
        newContext.also { newContext.show() }
    }
}

private fun help(storage: GameStorage) = Command(commandHelpMsg = "help - Shows the command list")
{ context, _ ->
    println("")
    getAllCommands(storage)
        .forEach { (_, cmd) -> println(cmd.commandHelpMsg) }
    println("")
    context
}

private val Play: Command = Command("play <position> - Plays a move at the specified coordinate.") {
    context, args ->
    if (context !is CommandContext.WithGame) {
        throw CommandException.IllegalContext("No game in progress")
    }
    val coordinate = when{
        args.size > 1 -> throw CommandException.InvalidParameters(Play)
        args.isNotEmpty() -> args[0].toCoordinateOrNull() ?: throw CommandException.InvalidParameters(Play, "Invalid coordinate")
        else -> throw CommandException.InvalidParameters(Play)
    }

    val newContext = context.play(coordinate)
    newContext.also { newContext.show() }
}


private val Show = Command("show - Displays the board and current game state."){
    context, _ ->
    if (context !is CommandContext.WithGame) throw IllegalStateException("No game in progress")
    if (context is CommandContext.DistributedGame) println("You are player ${context.clash.sidePlayer} in game $context.clash.name}")
    display(context.reversi, context.showTargets)
    context
}

private val Pass = Command("pass - Passes the turn to the opponent (only if no moves are available)."){
    context, _ ->

    if (context !is CommandContext.WithGame) {
        throw CommandException.IllegalContext("No game in progress")
    }
    val newContext = context.pass()
    newContext.also { newContext.show() }
}


private val Targets: Command = Command("targets [ON|OFF] - Toggles the display of valid moves. With no argument, shows the current status.") { context, args ->
    if (context !is CommandContext.WithGame) {
        throw CommandException.IllegalContext("No game in progress")
    }

    val showTargets = when {
        args.size > 1 -> throw CommandException.InvalidParameters(Targets)
        args.isEmpty() -> {
            println(if (context.showTargets) "Targets are ON" else "Targets are OFF")
            return@Command context
        }
        args.first() == "on" -> true
        args.first() == "off" -> false
        else -> throw CommandException.InvalidParameters(Targets)
    }

    val newContext = context.copyWithNewTargets(showTargets)
    newContext.also { newContext.show() }
}