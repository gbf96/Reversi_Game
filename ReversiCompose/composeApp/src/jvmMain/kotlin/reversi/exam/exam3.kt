package reversi.exam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.Lifecycle
/*
fun main() = application {
    Window(::exitApplication, title= "TDS - Match Game") { MatchApp() }
}
@Composable
fun MatchApp() {
    val vm = remember { MatchViewModel(numOfPairs= 4) }
    Column(...) {
        CardGrid(vm.rows, vm.cols, vm::card, vm::flipCard)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
            Text("Fails: ${vm.game.fails}",fontSize= 32.sp)
            Button(vm::hide, enabled= vm.game.is2Turned()) { Text("Hide") }
            Button(vm::newGame, enabled= vm.game.isOver()) { Text("New Game") }
        } } }

enum class Face{ DOWN, UP, MATCHED }
data class Card(val pair: Int, val state: Face)
data class MatchGame(val cards: List<Card>, val fails: Int = 0)
fun MatchGame(numOfPairs: Int) = MatchGame(
    List(numOfPairs){ Card(it, Face.DOWN) }.let { it+it }.shuffled()
)
fun MatchGame.isOver() = cards.all { it.state == Face.MATCHED }
fun MatchGame.is2Turned() = cards.count { it.state == Face.UP } == 2

fun MatchGame.hideTurned(): MatchGame{
    var count = 0
    check(cards.count { it.state == Face.UP } == 2)
    val new = cards.map { if(it.state == Face.UP) {
        count++
        Card(it.pair, Face.DOWN)
    } else it}

    return this.copy(cards = new, fails = fails+1)
}

fun MatchGame.flipCard(index: Int): MatchGame {
    // 1. Validar Argumento (Tem de ser o primeiro!)
    require(index in cards.indices) { "Índice inválido" }

    // 2. Validar Estado
    // check lança IllegalState se for falso. Queremos garantir que há menos de 2 viradas.
    check(cards.count { it.state == Face.UP } < 2) { "Já existem 2 cartas viradas" }

    val cardToFlip = cards[index]
    check(cardToFlip.state == Face.DOWN) { "A carta não está virada para baixo" }

    val otherCardUp = cards.find { it.state == Face.UP }

    val newCards = if (otherCardUp != null && otherCardUp.pair == cardToFlip.pair) {
        cards.mapIndexed { idx, card ->
            if (idx == index || card == otherCardUp) card.copy(state = Face.MATCHED)
            else card
        }
    } else {
        cards.mapIndexed { idx, card ->
            if (idx == index) card.copy(state = Face.UP)
            else card
        }
    }

    return copy(cards = newCards)
}

@Composable
fun CardGrid(rows: Int, cols: Int, cardGetter: (row:Int, col:Int) -> Card, onClick: (row:Int, col:Int) -> Unit){
    Column {
        repeat(rows){
            rIdx ->
            Row {
                repeat(cols){
                    cIdx ->
                    Column {
                        val card = cardGetter(rIdx, cIdx)
                        CardView(card){
                            if (card.state == Face.DOWN){
                                onClick(rIdx, cIdx)
                            }
                        }
                    }
                }
            }
        }
    }
}

class MatchViewModel(val numOfPairs: Int){
    var game by mutableStateOf(MatchGame(numOfPairs))
        private set
    val cols = 4
    val rows = numOfPairs / cols

    fun card(row:Int, col:Int): Card{
        val idx = row * cols + col
        return game.cards[idx]
    }

    fun flipCard(row:Int, col:Int){
        val idx = row  * cols + col
        if (!game.isOver() && !game.is2Turned()) game = game.flipCard(idx)
    }

    fun hide(){
        game = game.hideTurned()
    }

    fun newGame(){
        game = MatchGame(numOfPairs)
    }
}

*/