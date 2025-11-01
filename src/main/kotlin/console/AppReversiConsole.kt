package console

import model.Name
import model.Reversi
import storage.GameSerializer
import storage.TextFileStorage

object AppReversiConsole {
    fun run() {
        val st = TextFileStorage<Name, Reversi>("savedGames", GameSerializer)
        var context: CommandContext = CommandContext.Empty
        println("Welcome to Reversi")
        while (true) {
            print("> ")
            val (cmdStr, args) = readCommandOrNull() ?: continue
            try {
                context = cmdStr.toCommand(st).execute(context,args)
            }
            catch (_: CommandException.Unknown){
                println("Unknown command: $cmdStr")
            }
            catch (i: CommandException.InvalidParameters){
                print(i.message)
                println("Use: " + i.command.commandHelpMsg)
            }
            catch (e: Exception) {
                println("Error occurred: ${e.message}")
            }
        }
    }
}