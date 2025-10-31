package consoleFP

sealed class CommandException(message: String): Exception(message){
    class InvalidParameters(val command: Command, message: String = ""): CommandException(message)

    class Unknown(message: String = ""): CommandException(message)

    class IllegalContext(message: String = ""): CommandException(message)
}

