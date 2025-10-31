package model

sealed class GameState{
    data class Win(val winner: PiecesColor): GameState()
    data object Draw: GameState()
    data object Run: GameState()
    data class Pass(val passer: PiecesColor): GameState()
}


