package model

import storage.Storage
import java.lang.IllegalArgumentException

typealias GameStorage = Storage<Name, Reversi>

data class Clash(
    val storage: GameStorage,
    val name: Name,
    val sidePlayer: PiecesColor,
    override val reversi: Reversi,
    val showTargets: Boolean): Game{
    override fun play(coordinate: Coordinate): Clash? {
        if (reversi.currentPlayer != this.sidePlayer && !reversi.isGameOver())
            throw IllegalStateException("It's not your turn!")


        val newReversi = reversi.play(coordinate) ?: return null

        storage.update(name, newReversi)
        return this.copy(reversi = newReversi)
    }

    override fun pass(): Clash? {
        if (reversi.currentPlayer != this.sidePlayer && !reversi.isGameOver())
            throw IllegalStateException("It's not your turn!")

        val newReversi = reversi.pass()
            ?: return null

        storage.update(name, newReversi)
        return this.copy(reversi = newReversi)
    }

    companion object{
        fun start(name: Name, st: GameStorage, color: PiecesColor): Clash {
            if (st.read(name) != null) {
                throw IllegalArgumentException("File '$name' already exists.")
            }

            return Clash(st, name, color, Reversi(color), false).also {
                st.create(name, it.reversi)
            }
        }

        fun join(name: Name, st: GameStorage): Clash {
            val loadedReversi = st.read(name)
                ?: throw IllegalArgumentException("Game $name does not exist")
            val sidePlayer = when(loadedReversi.countPieces()){
                4 -> loadedReversi.currentPlayer.other()
                5 -> loadedReversi.currentPlayer
                else -> throw IllegalStateException("Can´t join in game $name")
            }
            return Clash(st, name, sidePlayer, loadedReversi, false)
        }
    }
}

fun Clash.refresh(): Clash{
    if (reversi.isGameOver())
        throw IllegalStateException("Game ${name.value} is over")

    return this.copy(reversi = storage.read(name) ?: throw IllegalStateException("Game file ${name.value} not found!"))
}


