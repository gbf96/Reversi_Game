package consoleFP

import model.Name
import model.Reversi
import storage.GameSerializer
import storage.TextFileStorage

object AppReversiConsole {
    fun run() {
        val st = TextFileStorage<Name, Reversi>("savedGames", GameSerializer)
        var context: CommandContext = CommandContext.Empty

        while (true) {
            print("$ ")
            val (cmdStr, args) = readCommandOrNull() ?: continue
            try {
                context = cmdStr.toCommand(st).execute(context,args)
            }
            catch (b: CommandException.Unknown){
                println("Unknown command: $cmdStr")
            }
            catch (i: CommandException.InvalidParameters){
                println(i.command.commandHelpMsg)
            }
            catch (e: Exception) {
                println("Error occurred: ${e.message}")
            }
        }
    }
}