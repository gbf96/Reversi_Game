package console

/**
 * Represents exceptions that may occur during the execution of commands.
 *
 * @param message The error message describing the exception.
 */
sealed class CommandException(message: String): Exception(message){
    /**
     * Represents an exception thrown when a command is invoked with invalid parameters.
     *
     * @param command The command whose parameters are invalid.
     * @param message An optional message providing additional details about the invalid parameters.
     */
    class InvalidParameters(val command: Command, message: String = ""): CommandException(message)

    /**
     * Represents an exception indicating that an unknown error has occurred during the execution of a command.
     *
     * @param message An optional message describing the unknown error.
     */
    class Unknown(message: String = ""): CommandException(message)

    /**
     * Represents an exception thrown when a command is executed in an invalid context.
     *
     * @param message An optional message providing additional details about the contextual violation.
     */
    class IllegalContext(message: String = ""): CommandException(message)
}

