package reversi.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import model.Name
import java.awt.Dialog

@Composable
fun JoinDialog(onDismiss: () -> Unit, joinAction: (Name) -> Unit){
    Dialog(
        onDismissRequest = onDismiss,
    ){
        joinDialogContent(onDismiss, joinAction)
    }
}

@Composable
fun joinDialogContent(onDismiss: () -> Unit, joinAction: (Name) -> Unit){
    var name by remember { mutableStateOf("") }
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(150.dp),
    ){
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                value = name,
                onValueChange = { newValue -> name = newValue },
                label = {
                    Text("Name of Game")
                }
            )
            val isEnabled = Name.isValid(name)
            Button(
                enabled = isEnabled,
                onClick = {
                    joinAction(Name(name))
                    onDismiss()
                }
            ){
                Text("Join Game")
            }
        }
    }

}