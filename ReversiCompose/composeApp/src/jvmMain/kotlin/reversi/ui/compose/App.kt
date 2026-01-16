package reversi.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import model.Game
import model.Name
import model.Reversi

import org.jetbrains.compose.resources.imageResource
import reversi.exithandling.ExitHandler
import reversi.viewmodel.AppViewModel
import reversi.storage.TextFileStorage
import reversi.storage.mongo.MongoDriver
import reversi.storage.mongo.MongoStorage
import reversicompose.composeapp.generated.resources.Res
import reversicompose.composeapp.generated.resources.sprites1
import storage.GameSerializer


@Composable
fun FrameWindowScope.App() {
    MaterialTheme{
        val spriteSheet = imageResource(Res.drawable.sprites1)
        var scope = rememberCoroutineScope()
        //val vm = remember { AppViewModel(TextFileStorage("savedGames", GameSerializer)) }
        val vm = remember {
            //val st = TextFileStorage<Name, Reversi>("savedGames", GameSerializer)
            val driver = MongoDriver("ReversiMongo")
            val st = MongoStorage<Name, Reversi>("savedGames", driver, GameSerializer)
            val myVm = AppViewModel(st, scope)
            myVm
        }
        Column {
            MenuBar {
                Menu("Game"){
                    Item("New" , onClick = vm::toggleShowNewDialog)
                    Item("Join", onClick = vm::toggleShowJoinDialog )
                    Item(
                        text = "Refresh",
                        enabled = vm.canRefresh,
                        onClick = vm::refresh
                    )
                    Item("Exit", onClick = ExitHandler::exit)
                }
                Menu("Play"){
                    Item("Pass" , enabled = vm.canPass, onClick = vm::pass)
                }
                Menu("Options"){
                    CheckboxItem("Show Targets" , checked = vm.showTargets, onCheckedChange = vm::toggleShowTargets)
                    CheckboxItem("Auto-refresh" , checked = vm.autoRefresh, onCheckedChange = vm::toggleAutoRefresh)
                }
            }
            if(vm.showNewDialog){
                NewDialog(vm::toggleShowNewDialog, vm::startLocalGame ,vm::startClash, spriteSheet)
            }
            if(vm.showJoinDialog){
                JoinDialog(vm::toggleShowJoinDialog, vm::joinGame)
            }
            BoardView(vm.reversi, vm::play, vm.shouldShowTargets(), spriteSheet)
            StatusBar(vm.reversi, spriteSheet)
        }

    }
}