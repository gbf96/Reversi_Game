package reversi.exithandling

object ExitHandler {
    var exitHandlers = listOf<() -> Unit>()
        private set

    var exitApplication: (() -> Unit)? = null
        private set

    fun exit(){
        exitHandlers.forEach { it() }

        val exitApp = checkNotNull(exitApplication) { "ExitHandler is not configured" }
        exitApp()
    }

    fun registerExitHandler(exitHandler: () -> Unit) {
        exitHandlers = exitHandlers + exitHandler
    }

    fun registerExitApplication(exitApp: () -> Unit) {
        exitApplication = exitApp
    }
}