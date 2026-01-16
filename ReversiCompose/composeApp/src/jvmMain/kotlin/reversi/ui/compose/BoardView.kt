package reversi.ui.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import model.BOARD_SIDE
import model.Coordinate
import model.Game
import model.PiecesColor
import org.jetbrains.compose.resources.imageResource
import reversicompose.composeapp.generated.resources.Res
import reversicompose.composeapp.generated.resources.sprites1
import kotlin.math.roundToInt

@Composable
fun BoardView(reversi: Game?, clickAction: (Coordinate)->Unit, showTargets: Boolean, spriteSheet: ImageBitmap){
    val columnString = buildString {
        repeat(BOARD_SIDE) {
            i->
            append("${'A' + i}")
        }
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(700.dp)
            .height(700.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(Color.Gray)
                .border(1.dp, Color.Black)

        ) {

            Spacer(
                modifier = Modifier
                    .width(24.dp)
                    .background(color = Color.Gray)
            )
            repeat(BOARD_SIDE) { i ->
                Text(
                    text = ('A' + i).toString(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

        }


        Row (modifier = Modifier.fillMaxWidth()){
            Column(
                modifier = Modifier
                    .width(24.dp)
                    .background(Color.Gray)
                    .border(1.dp, Color.Black)
            ) {
                repeat(BOARD_SIDE) {
                    i->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = (i+1).toString(),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
            PiecesView(
                boardSide = BOARD_SIDE,
                modifier = Modifier
                    .weight(1f),
                reversi = reversi,
                clickAction = clickAction,
                showTargets = showTargets,
                spriteSheet = spriteSheet
            )
        }
    }
}

@Composable
fun PiecesView(
    boardSide: Int,
    modifier: Modifier = Modifier,
    reversi: Game?,
    clickAction: (Coordinate) -> Unit,
    showTargets: Boolean
    , spriteSheet: ImageBitmap
) {

    Column(modifier = modifier) {
        repeat(boardSide) { row ->
            Row(modifier = Modifier.weight(1f)) {
                repeat(boardSide) { col ->
                    PieceView(
                        coordinate = Coordinate(row, col),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        reversi = reversi,
                        clickAction = clickAction,
                        showTargets = showTargets,
                        spriteSheet = spriteSheet
                    )
                }
            }
        }
    }
}

@Composable
fun PieceView(
    modifier: Modifier = Modifier,
    coordinate: Coordinate,
    reversi: Game?,
    clickAction: (Coordinate)-> Unit,
    showTargets: Boolean
    , spriteSheet: ImageBitmap
) {
    Box(
        modifier = modifier
            .background(color = Color.Green)
            .border(width = 0.5.dp, color = Color.Black)
            .clickable { clickAction(coordinate) },
        contentAlignment = Alignment.Center
    ) {
        val piece = reversi?.reversi?.get(coordinate)

        if (piece != null) {
            AnimatedReversiPiece(targetColor = piece, spriteSheet)
        }
        else {
            if (showTargets && reversi?.reversi?.validTargets?.contains(coordinate) == true ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.3f)
                        .background(Color.Yellow, CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                )
            }
        }
    }
}



@Composable
fun ReversiPiece(color: Color, modifier: Modifier = Modifier
    .fillMaxSize(0.8f)
    .background(color, CircleShape)
    .border(1.dp, Color.Gray, CircleShape)) {
    Box(
        modifier = modifier
    )
}


@Composable
fun AnimatedReversiPiece(
    targetColor: PiecesColor,
    spriteSheet: ImageBitmap,
    modifier: Modifier = Modifier
) {


    val frameCoordinates = remember {
        listOf(
            Pair(0, 6),
            Pair(0, 7),
            Pair(1, 0),
            Pair(1, 1),
            Pair(1, 2),
            Pair(1, 3),
            Pair(1, 4),
            Pair(1, 5),
            Pair(1, 6)
        )
    }

    val maxFrameIndex = frameCoordinates.lastIndex


    val targetIndex = if (targetColor == PiecesColor.WHITE) 0f else maxFrameIndex.toFloat()

    val animatedIndex by animateFloatAsState(
        targetValue = targetIndex,
        animationSpec = tween(durationMillis = 500)
    )

    Canvas(modifier = modifier.fillMaxSize(0.85f)) {
        val cols = 8
        val rows = 4
        val frameWidth = spriteSheet.width / cols
        val frameHeight = spriteSheet.height / rows

        val currentIndex = animatedIndex.roundToInt().coerceIn(0, maxFrameIndex)

        val (row, col) = frameCoordinates[currentIndex]

        val srcX = col * frameWidth
        val srcY = row * frameHeight

        drawImage(
            image = spriteSheet,
            srcOffset = IntOffset(srcX, srcY),
            srcSize = IntSize(frameWidth, frameHeight),
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
            filterQuality = FilterQuality.Medium
        )
    }
}