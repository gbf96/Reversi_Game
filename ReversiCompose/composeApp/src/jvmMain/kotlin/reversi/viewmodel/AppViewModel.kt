package reversi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Clash
import model.Coordinate
import model.Game
import model.GameStorage
import model.Name
import model.NoChangesException
import model.PiecesColor
import model.Reversi
import model.canPass
import model.isMyTurn
import model.refresh


class AppViewModel(val storage: GameStorage, val scope: CoroutineScope) {
    var reversi by mutableStateOf<Game?>(null)
        private set
    var showNewDialog by mutableStateOf(false)
        private set
    var showJoinDialog by mutableStateOf(false)
        private set
    private var _showTargets by mutableStateOf(false)
    var isWaiting: Boolean by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var showTargets by mutableStateOf(false)
        private set

    var autoRefresh by mutableStateOf(false)
        private set

    fun shouldShowTargets(): Boolean {
        return if (reversi is Clash) {
            showTargets && (reversi as Clash).isMyTurn()
        } else {
            showTargets
        }
    }

    val canPass: Boolean
        get() = reversi?.reversi?.canPass() == true

    val canRefresh: Boolean
        get() = (reversi is Clash) && !autoRefresh && (!(reversi as Clash).isMyTurn())
    fun toggleShowNewDialog(){
        showNewDialog = !showNewDialog
    }
    fun toggleShowJoinDialog(){
        showJoinDialog = !showJoinDialog
    }

    fun toggleShowTargets(show: Boolean) {
        showTargets = show
    }

    fun toggleAutoRefresh(auto: Boolean){
        autoRefresh = auto
        if (autoRefresh && reversi is Clash) {
            waitForOtherSide()
        }
    }
    fun exec(action: Game.() -> Game?) {
        try {
            val newGame = reversi?.action()
            if (newGame != null) {
                reversi = newGame
            }
        } catch (e: Exception) {
        }
    }

    fun startClash(name: Name, firstPlayerColor: PiecesColor){
        reversi = (Clash.start(name, storage, firstPlayerColor))
    }

    fun startLocalGame(firstPlayerColor: PiecesColor){
        reversi = Reversi(firstPlayerColor)
    }

    fun joinGame(name: Name) {
        reversi = Clash.join(name, storage)
        if (autoRefresh) {
            waitForOtherSide()
        }
    }

    fun play(pos: Coordinate): Unit {
        exec { play(pos) }
        if (reversi is Clash && autoRefresh){
            waitForOtherSide()
        }
    }

    private suspend fun performRefresh() {
        try {
            if (reversi is Clash) {
                val newGame = (reversi as Clash).refresh()
                reversi = newGame
            }
        } catch (_: NoChangesException) {
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    fun waitForOtherSide() {
        if (isWaiting) return
        isWaiting = true

        scope.launch {
            while (isWaiting && autoRefresh) {
                delay(3000)
                performRefresh()
            }
            isWaiting = false
        }
    }

    fun refresh() {
        scope.launch {
            performRefresh()
        }
    }

    fun pass() =
        exec { this.pass() }
}