package reversi.ui.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import model.Name
import model.PiecesColor

@Composable
fun NewDialog(onDismiss: () -> Unit, startLocalGame: (PiecesColor)-> Unit, startClash: (Name, PiecesColor) -> Unit, spriteSheet: ImageBitmap) {
    Dialog(
        onDismissRequest = onDismiss,
    ){
        NewDialogContent(onDismiss, startLocalGame, startClash, spriteSheet)
    }
}

@Composable
@Preview
fun NewDialogContent(onDismiss: () -> Unit, startLocalGame: (PiecesColor)-> Unit, startClash: (Name, PiecesColor) -> Unit, spriteSheet: ImageBitmap) {
    var name by remember { mutableStateOf("") }
    var multiplayer by remember { mutableStateOf(true) }
    var selectedColor by remember { mutableStateOf(PiecesColor.BLACK) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = multiplayer,
                    onCheckedChange = { multiplayer = it }
                )
                Text("Multiplayer")
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (multiplayer){
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    value = name,
                    onValueChange = { newValue -> name = newValue },
                    label = {
                        Text("Name of Game")
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable {
                        selectedColor = if (selectedColor == PiecesColor.BLACK) PiecesColor.WHITE else PiecesColor.BLACK
                    }
                    .padding(8.dp)
            ) {
                Text("Player: ")
                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier.size(40.dp)
                ) {
                    AnimatedReversiPiece(targetColor = selectedColor, spriteSheet)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val newGameEnabled = (multiplayer && Name.isValid(name)) || !multiplayer
            Button(
                onClick = { if (multiplayer) {
                    startClash(Name(name), selectedColor)
                    onDismiss()
                } else {
                    startLocalGame(selectedColor)
                    onDismiss()
                }
                },
                enabled = newGameEnabled,
            ){
                Text("New Game")
            }
        }
    }
}