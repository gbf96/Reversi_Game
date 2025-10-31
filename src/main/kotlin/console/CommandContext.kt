package console

import model.Clash
import model.Coordinate
import model.Reversi
import model.pass
import model.play

sealed interface CommandContext {

    interface WithGame : CommandContext{
        val reversi: Reversi
        val showTargets: Boolean

        fun copyWithNewTargets(newShowTargets: Boolean): WithGame
        fun play(coordinate: Coordinate): WithGame
        fun show() = display(this.reversi, this.showTargets)
        fun pass(): WithGame
    }

    object Empty : CommandContext

    data class LocalGame(
        override val reversi: Reversi,
        override val showTargets: Boolean
    ) : CommandContext, WithGame {

        override fun copyWithNewTargets(newShowTargets: Boolean): WithGame {
            return this.copy(showTargets = newShowTargets)
        }

        override fun play(coordinate: Coordinate): WithGame {
            val newReversi = reversi.play(coordinate) ?: throw IllegalArgumentException("Invalid move")
            return this.copy(reversi = newReversi)
        }

        override fun pass(): WithGame {
            val newReversi = reversi.pass() ?: throw IllegalArgumentException("You can't pass")
            return this.copy(reversi = newReversi)
        }
    }

    data class DistributedGame(
        val clash: Clash,
        override val showTargets: Boolean
    ) : CommandContext, WithGame {
        override val reversi: Reversi
            get() = clash.reversi

        override fun copyWithNewTargets(newShowTargets: Boolean): WithGame {
            return this.copy(showTargets = newShowTargets)
        }

        override fun play(coordinate: Coordinate): WithGame {
            val newClashState = clash.play(coordinate) ?: throw IllegalArgumentException("Invalid move")
            return this.copy(clash = newClashState)
        }

        override fun pass(): WithGame {
            val newClash = clash.pass() ?: throw IllegalArgumentException("You can't pass")
            return this.copy(clash = newClash)
        }
    }
}