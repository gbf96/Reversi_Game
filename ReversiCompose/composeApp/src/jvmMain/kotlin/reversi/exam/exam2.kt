package reversi.exam

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import java.awt.Button
/*
fun main() { application {
    Window(title="CheckItem App", onCloseRequest = ::exitApplication) {
        MaterialTheme { CheckItemApp() }
    }
} }
@Composable
private fun CheckItemApp() {
    var checkItemList by remember { mutableStateOf(CheckItemList()) }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ItemViewer(checkItemList.checkItems) { clicked: CheckItem ->
            checkItemList = checkItemList.toggleState(clicked)
        }
        ItemAdder(validator = { it.isNotBlank() && it.length >= 3 }) { name ->
            checkItemList = checkItemList.add(name)
        }
    }
}

data class CheckItem(val name: String, val checked: Boolean = false)
data class CheckItemList(val checkItems: List<CheckItem> = emptyList())

fun CheckItemList.add(name: String): CheckItemList {
    require(name.isNotBlank())
    return if (checkItems.any { it.name==name }) this
    else CheckItemList(checkItems + CheckItem(name))
}

fun CheckItemList.toggleState(item: CheckItem) = CheckItemList(
    checkItems.map{ if(it == item) it.copy(checked = !it.checked) else it}
)

@Composable
fun ItemViewer(itemList: List<CheckItem>, action: (CheckItem) -> Unit){
    val size = itemList.size
    Column {
        repeat(size){
            idx ->

            var img1 = if(itemList[idx].checked) "" else " "
            Image(
                painter = img,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = itemList[idx].name,
                fontStyle = MaterialTheme.typography.h4.fontStyle

            )

        }
    }
}

@Composable
fun ItemAdder(validator: (String) -> Boolean, adder: (String) -> Unit){
    var text by remember { mutableStateOf("") }
    val isValid = validator(text)
    Row {
        TextField(
            value = text,
            onValueChange = {text = it},
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = { adder(text.trim() ); text = ""},
            enabled = isValid

        ){
            Text("Add")
        }
    }
}
*/
