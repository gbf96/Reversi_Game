package reversi.ui.compose

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import reversi.exithandling.ExitHandler

fun main() = application {
    ExitHandler.registerExitApplication (::exitApplication)
    Window(
        state = WindowState(size = DpSize.Unspecified),
        onCloseRequest = ExitHandler::exit,
        title = "Reversi",
        resizable = false
    ){
        App()
    }
}