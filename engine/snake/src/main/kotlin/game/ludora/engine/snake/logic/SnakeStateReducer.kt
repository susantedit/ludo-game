package game.ludora.engine.snake.logic

import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.DiceRoller
import game.ludora.engine.snake.model.SnakeAction
import game.ludora.engine.snake.model.SnakeBoard
import game.ludora.engine.snake.model.SnakeEvent
import game.ludora.engine.snake.model.SnakeGameState
import game.ludora.engine.snake.model.SnakePlayerState
import game.ludora.engine.snake.model.SnakeTurnPhase

/**
 * Pure state reducer executing deterministic transitions for Classic Snake & Ladder.
 */
object SnakeStateReducer {

    fun reduce(
        state: SnakeGameState,
        action: SnakeAction,
        diceRoller: DiceRoller? = null
    ): Pair<SnakeGameState, List<SnakeEvent>> {
        if (state.isGameOver) return state to emptyList()

        return when (action) {
            is SnakeAction.RollDice -> handleRoll(state, action, diceRoller)
            is SnakeAction.PassTurn -> passTurn(state, emptyList())
        }
    }

    private fun handleRoll(
        state: SnakeGameState,
        action: SnakeAction.RollDice,
        diceRoller: DiceRoller?
    ): Pair<SnakeGameState, List<SnakeEvent>> {
        val roll = action.forcedValue ?: (diceRoller?.roll() ?: 1)
        val activePlayer = state.activePlayer
        val events = mutableListOf<SnakeEvent>()

        // 1. Check three consecutive sixes
        if (roll == 6) {
            val consecutiveSixes = activePlayer.consecutiveSixes + 1
            events.add(
                SnakeEvent.DiceRolled(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    value = 6,
                    consecutiveSixes = consecutiveSixes
                )
            )

            if (consecutiveSixes == 3) {
                // Third six is forfeited immediately; turn passes
                events.add(
                    SnakeEvent.ThreeSixesPenalty(
                        playerColor = activePlayer.color,
                        seatIndex = activePlayer.seatIndex
                    )
                )
                val resetPlayer = activePlayer.copy(consecutiveSixes = 0)
                val intermediate = state.updatePlayer(resetPlayer)
                return passTurn(intermediate, events)
            }
        } else {
            events.add(
                SnakeEvent.DiceRolled(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    value = roll,
                    consecutiveSixes = 0
                )
            )
        }

        // 2. Calculate movement transition
        val transition = SnakeBoard.calculateDestination(activePlayer.currentSquare, roll)

        if (transition.isOvershoot) {
            events.add(
                SnakeEvent.OvershootStalled(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    currentSquare = activePlayer.currentSquare,
                    roll = roll
                )
            )
            val updatedPlayer = activePlayer.copy(consecutiveSixes = 0)
            val intermediate = state.updatePlayer(updatedPlayer)
            return passTurn(intermediate, events)
        }

        // 3. Move token
        val fromSquare = activePlayer.currentSquare
        val landedSquare = transition.landedSquare
        val finalSquare = transition.finalSquare

        events.add(
            SnakeEvent.TokenAdvanced(
                playerColor = activePlayer.color,
                seatIndex = activePlayer.seatIndex,
                fromSquare = fromSquare,
                toSquare = landedSquare
            )
        )

        if (transition.isLadder) {
            events.add(
                SnakeEvent.LadderClimbed(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    baseSquare = landedSquare,
                    topSquare = finalSquare
                )
            )
        } else if (transition.isSnake) {
            events.add(
                SnakeEvent.SnakeDropped(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    headSquare = landedSquare,
                    tailSquare = finalSquare
                )
            )
        }

        val updatedActivePlayer = activePlayer.copy(
            currentSquare = finalSquare,
            consecutiveSixes = if (roll == 6) activePlayer.consecutiveSixes + 1 else 0
        )
        var updatedPlayers = state.players.map {
            if (it.seatIndex == activePlayer.seatIndex) updatedActivePlayer else it
        }

        // 4. Check victory condition (square 100 reached)
        if (finalSquare == 100) {
            val rank = state.winners.size + 1
            val finishedPlayer = updatedActivePlayer.copy(rank = rank)
            updatedPlayers = updatedPlayers.map {
                if (it.seatIndex == activePlayer.seatIndex) finishedPlayer else it
            }
            val newWinners = state.winners + activePlayer.color

            events.add(
                SnakeEvent.PlayerFinished(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    rank = rank
                )
            )

            // In 2-player or multiplayer match, when winner reaches 100, finalize match
            val remaining = updatedPlayers.filterNot { it.isFinished }
                .sortedByDescending { it.currentSquare }

            val allStandings = newWinners + remaining.map { it.color }
            val rankedRemaining = remaining.mapIndexed { idx, p ->
                p.copy(rank = rank + 1 + idx)
            }

            val finalPlayers = updatedPlayers.map { p ->
                rankedRemaining.firstOrNull { it.seatIndex == p.seatIndex } ?: p
            }

            events.add(
                SnakeEvent.MatchCompleted(
                    standings = allStandings,
                    reason = "PLAYER_REACHED_GOAL"
                )
            )

            val finalState = state.copy(
                players = finalPlayers,
                currentRoll = roll,
                phase = SnakeTurnPhase.GAME_OVER,
                winners = allStandings,
                isGameOver = true
            )
            return finalState to events
        }

        // 5. Bonus roll check: rolling 6 grants bonus roll
        if (roll == 6) {
            events.add(
                SnakeEvent.BonusRollGranted(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex
                )
            )
            val nextState = state.copy(
                players = updatedPlayers,
                currentRoll = roll,
                phase = SnakeTurnPhase.WAITING_FOR_ROLL
            )
            return nextState to events
        }

        // 6. Turn passes to next player
        val intermediateState = state.copy(
            players = updatedPlayers,
            currentRoll = roll
        )
        return passTurn(intermediateState, events)
    }

    private fun passTurn(
        state: SnakeGameState,
        accumulatedEvents: List<SnakeEvent>
    ): Pair<SnakeGameState, List<SnakeEvent>> {
        val events = accumulatedEvents.toMutableList()
        val currentIndex = state.players.indexOfFirst { it.seatIndex == state.activeSeatIndex }

        var nextIndex = (currentIndex + 1) % state.players.size
        var roundsIncrement = if (nextIndex <= currentIndex) 1 else 0

        var attempts = 0
        while (state.players[nextIndex].isFinished && attempts < state.players.size) {
            val prev = nextIndex
            nextIndex = (nextIndex + 1) % state.players.size
            if (nextIndex <= prev) {
                roundsIncrement++
            }
            attempts++
        }

        val newRoundCount = state.roundCount + roundsIncrement

        // Anti-Stall 200 Rounds Ceiling
        if (newRoundCount > state.maxRounds) {
            val remaining = state.players.filterNot { it.isFinished }
                .sortedByDescending { it.currentSquare }
            val allStandings = state.winners + remaining.map { it.color }

            events.add(
                SnakeEvent.MatchCompleted(
                    standings = allStandings,
                    reason = "TIME_LIMIT_REACHED"
                )
            )

            val finalState = state.copy(
                roundCount = state.maxRounds,
                phase = SnakeTurnPhase.GAME_OVER,
                isGameOver = true,
                winners = allStandings
            )
            return finalState to events
        }

        val nextPlayer = state.players[nextIndex]
        events.add(
            SnakeEvent.TurnPassed(
                previousSeatIndex = state.activeSeatIndex,
                nextSeatIndex = nextPlayer.seatIndex,
                nextPlayerColor = nextPlayer.color,
                roundCount = newRoundCount
            )
        )

        // Reset consecutive sixes on seat change
        val updatedPlayers = state.players.map {
            if (it.seatIndex == state.activeSeatIndex) it.copy(consecutiveSixes = 0) else it
        }

        val nextState = state.copy(
            players = updatedPlayers,
            activeSeatIndex = nextPlayer.seatIndex,
            currentRoll = null,
            phase = SnakeTurnPhase.WAITING_FOR_ROLL,
            roundCount = newRoundCount
        )

        return nextState to events
    }

    private fun SnakeGameState.updatePlayer(updated: SnakePlayerState): SnakeGameState =
        copy(players = players.map { if (it.seatIndex == updated.seatIndex) updated else it })
}
