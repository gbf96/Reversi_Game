package reversi.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
/*
enum class DiceFace { ONE, TWO, THREE, FOUR, FIVE, SIX }
typealias Roll = List<DiceFace>
class DiceThrower(val numOfDice: Int = 2) {
    init {
        require(numOfDice in 1..4)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Dice Thrower")
    { MaterialTheme { DiceThrowerApp() } }
}
@Composable
fun DiceThrowerApp() {
    val vm = remember { DiceViewModel() }
    Column( ... ) {
        NumOfDiceEditor(vm.numOfDice) { vm.setRollingDice(it) }
        RollView(vm.numOfDice, vm.roll)
        if (vm.roll==null) Button(vm::throwDice) { Text("Throw") }
        else Button(vm::catchDice) { Text("Catch") }
    }
}

private fun rollDice():DiceFace = DiceFace.entries.random()

private fun DiceThrower.roll():Roll = List(numOfDice) { rollDice() }

class DiceViewModel(){
    private var d = DiceThrower()
    var numOfDice by mutableStateOf(d.numOfDice)
        private set
    var roll by mutableStateOf<Roll?>(null)
        private set


    fun setRollingDice(num: Int) {
        if (num !in 1..4) return
        d = DiceThrower(num)
        numOfDice = d.numOfDice
        roll = null
    }

    fun throwDice(){
        roll = d.roll()
    }

    fun catchDice(){
        roll = null
    }
}

@Composable
fun NumOfDiceEditor(numOfDice: Int, action: (Int) -> Unit){
    OutlinedTextField(
        value = numOfDice.toString(),
        onValueChange = { action(it.toIntOrNull() ?: 2) },
        label = {Text("number of dice")}
    )
}

@Composable
fun RollView(numOfDice: Int, roll: Roll?){
    Row (horizontalArrangement = Arrangement.spacedBy(8.dp)){
        repeat(numOfDice){
            idx ->
            DiceView(roll?.get(idx))
        }
    }
}
*/