package reversi.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Clash
import model.Game
import model.GameState
import model.PiecesColor
import model.Reversi
import model.countPieces

@Composable
fun StatusBar(reversi: Game?, spriteSheet: ImageBitmap) {
    Row(
        modifier = Modifier
            .width(700.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (reversi != null) {
            GameStatusLeft(reversi.reversi, spriteSheet)
            if (reversi is Clash) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "You: ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    StatusPieceIcon(color = reversi.sidePlayer, spriteSheet = spriteSheet)
                }
            }
            GameScoreRight(reversi.reversi, spriteSheet)
        } else {
            Text(
                text = "Start a game to play",
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GameStatusLeft(game: Reversi, spriteSheet: ImageBitmap) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (val state = game.gameState) {
            is GameState.Win -> {
                Text(
                    text = "Winner: ",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusPieceIcon(color = state.winner, spriteSheet)
            }
            GameState.Draw -> {
                Text(
                    text = "Draw",
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
            }
            else -> {
                Text(
                    text = "Turn: ",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusPieceIcon(color = game.currentPlayer, spriteSheet = spriteSheet)
            }
        }
    }
}

@Composable
private fun GameScoreRight(game: Reversi, spriteSheet: ImageBitmap) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${game.countPieces(PiecesColor.WHITE)} x ",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        StatusPieceIcon(color = PiecesColor.WHITE, spriteSheet)

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = "${game.countPieces(PiecesColor.BLACK)} x ",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        StatusPieceIcon(color = PiecesColor.BLACK, spriteSheet = spriteSheet)
    }
}


@Composable
private fun StatusPieceIcon(color: PiecesColor, spriteSheet: ImageBitmap) {
    Box(
        modifier = Modifier.size(32.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedReversiPiece(targetColor = color, spriteSheet = spriteSheet)
    }
}