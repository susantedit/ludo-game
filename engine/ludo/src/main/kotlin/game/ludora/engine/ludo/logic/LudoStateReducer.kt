package game.ludora.engine.ludo.logic

import game.ludora.core.model.PlayerColor
import game.ludora.engine.core.DiceRoller
import game.ludora.engine.ludo.model.BonusRollReason
import game.ludora.engine.ludo.model.LudoAction
import game.ludora.engine.ludo.model.LudoBoard
import game.ludora.engine.ludo.model.LudoEvent
import game.ludora.engine.ludo.model.LudoGameState
import game.ludora.engine.ludo.model.LudoPlayerState
import game.ludora.engine.ludo.model.LudoPosition
import game.ludora.engine.ludo.model.LudoToken
import game.ludora.engine.ludo.model.LudoTurnPhase

/**
 * Pure state reducer executing deterministic transitions for the Ludo engine.
 */
object LudoStateReducer {

    fun reduce(
        state: LudoGameState,
        action: LudoAction,
        diceRoller: DiceRoller? = null
    ): Pair<LudoGameState, List<LudoEvent>> {
        if (state.isGameOver) return state to emptyList()

        return when (action) {
            is LudoAction.RollDice -> handleRollDice(state, action, diceRoller)
            is LudoAction.SelectMove -> handleSelectMove(state, action)
            is LudoAction.PassTurn -> passTurnToNextPlayer(state, emptyList())
        }
    }

    private fun handleRollDice(
        state: LudoGameState,
        action: LudoAction.RollDice,
        diceRoller: DiceRoller?
    ): Pair<LudoGameState, List<LudoEvent>> {
        if (state.phase != LudoTurnPhase.WAITING_FOR_ROLL) return state to emptyList()

        val roll = action.forcedValue ?: (diceRoller?.roll() ?: 1)
        val activePlayer = state.activePlayer
        val events = mutableListOf<LudoEvent>()

        if (roll == 6) {
            val consecutiveSixes = activePlayer.consecutiveSixes + 1
            events.add(
                LudoEvent.DiceRolled(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    value = 6,
                    consecutiveSixes = consecutiveSixes
                )
            )

            if (consecutiveSixes == 3) {
                // Three consecutive sixes penalty: third roll forfeited, turn passes immediately
                events.add(
                    LudoEvent.ThreeSixesPenalty(
                        playerColor = activePlayer.color,
                        seatIndex = activePlayer.seatIndex
                    )
                )
                val updatedPlayer = activePlayer.copy(consecutiveSixes = 0)
                val intermediateState = state.updatePlayer(updatedPlayer)
                return passTurnToNextPlayer(intermediateState, events)
            } else {
                val legalMoves = LudoMoveValidator.getLegalMoves(state, 6)
                if (legalMoves.isEmpty()) {
                    events.add(
                        LudoEvent.NoLegalMoves(
                            playerColor = activePlayer.color,
                            seatIndex = activePlayer.seatIndex,
                            roll = 6
                        )
                    )
                    val updatedPlayer = activePlayer.copy(consecutiveSixes = 0)
                    val intermediateState = state.updatePlayer(updatedPlayer)
                    return passTurnToNextPlayer(intermediateState, events)
                }

                val updatedPlayer = activePlayer.copy(consecutiveSixes = consecutiveSixes)
                val nextState = state.updatePlayer(updatedPlayer).copy(
                    currentRoll = 6,
                    phase = LudoTurnPhase.WAITING_FOR_MOVE
                )
                return nextState to events
            }
        } else {
            // Roll in 1..5
            events.add(
                LudoEvent.DiceRolled(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    value = roll,
                    consecutiveSixes = 0
                )
            )

            val legalMoves = LudoMoveValidator.getLegalMoves(state, roll)
            if (legalMoves.isEmpty()) {
                events.add(
                    LudoEvent.NoLegalMoves(
                        playerColor = activePlayer.color,
                        seatIndex = activePlayer.seatIndex,
                        roll = roll
                    )
                )
                val updatedPlayer = activePlayer.copy(consecutiveSixes = 0)
                val intermediateState = state.updatePlayer(updatedPlayer)
                return passTurnToNextPlayer(intermediateState, events)
            }

            val updatedPlayer = activePlayer.copy(consecutiveSixes = 0)
            val nextState = state.updatePlayer(updatedPlayer).copy(
                currentRoll = roll,
                phase = LudoTurnPhase.WAITING_FOR_MOVE
            )
            return nextState to events
        }
    }

    private fun handleSelectMove(
        state: LudoGameState,
        action: LudoAction.SelectMove
    ): Pair<LudoGameState, List<LudoEvent>> {
        if (state.phase != LudoTurnPhase.WAITING_FOR_MOVE) return state to emptyList()
        val roll = state.currentRoll ?: return state to emptyList()

        val legalMoves = LudoMoveValidator.getLegalMoves(state, roll)
        val selectedMove = legalMoves.firstOrNull { it.tokenId == action.tokenId } ?: return state to emptyList()

        val events = mutableListOf<LudoEvent>()
        val activePlayer = state.activePlayer

        // 1. Check if token was released from base
        if (selectedMove.from is LudoPosition.InBase && selectedMove.to is LudoPosition.OnTrack) {
            events.add(
                LudoEvent.TokenReleased(
                    playerColor = activePlayer.color,
                    tokenId = selectedMove.tokenId,
                    startStep = selectedMove.to.stepIndex
                )
            )
        }

        events.add(
            LudoEvent.TokenMoved(
                playerColor = activePlayer.color,
                tokenId = selectedMove.tokenId,
                from = selectedMove.from,
                to = selectedMove.to
            )
        )

        // 2. Update moved token
        val updatedTokens = activePlayer.tokens.map { token ->
            if (token.id == selectedMove.tokenId) token.copy(position = selectedMove.to) else token
        }
        var updatedActivePlayer = activePlayer.copy(tokens = updatedTokens)
        var updatedPlayers = state.players.map { player ->
            if (player.seatIndex == activePlayer.seatIndex) updatedActivePlayer else player
        }

        // 3. Handle opponent capture
        var isCapture = false
        if (selectedMove.isCapture && selectedMove.capturedOpponentToken != null) {
            val captured = selectedMove.capturedOpponentToken
            isCapture = true
            val opponent = updatedPlayers.first { it.color == captured.color }
            val newOpponentTokens = opponent.tokens.map { token ->
                if (token.id == captured.id) {
                    token.copy(position = LudoPosition.InBase(slotIndex = token.id))
                } else token
            }
            val updatedOpponent = opponent.copy(tokens = newOpponentTokens)
            updatedPlayers = updatedPlayers.map { if (it.color == captured.color) updatedOpponent else it }

            events.add(
                LudoEvent.TokenCaptured(
                    capturingColor = activePlayer.color,
                    capturedColor = captured.color,
                    capturedTokenId = captured.id,
                    stepIndex = (selectedMove.to as LudoPosition.OnTrack).stepIndex
                )
            )
        }

        // 4. Check if active player finished all tokens
        val hasPlayerFinished = updatedActivePlayer.isFinished
        var newWinners = state.winners
        if (hasPlayerFinished && updatedActivePlayer.rank == null) {
            val rank = state.winners.size + 1
            updatedActivePlayer = updatedActivePlayer.copy(rank = rank)
            updatedPlayers = updatedPlayers.map {
                if (it.seatIndex == updatedActivePlayer.seatIndex) updatedActivePlayer else it
            }
            newWinners = state.winners + updatedActivePlayer.color
            events.add(
                LudoEvent.PlayerFinished(
                    playerColor = updatedActivePlayer.color,
                    seatIndex = updatedActivePlayer.seatIndex,
                    rank = rank
                )
            )
        }

        // 5. Check if match completes naturally (e.g. only 1 unfinished player remains)
        val remainingUnfinished = updatedPlayers.filterNot { it.isFinished }
        val isMatchComplete = remainingUnfinished.size <= 1

        if (isMatchComplete) {
            // Assign last rank to the remaining player if any
            if (remainingUnfinished.isNotEmpty()) {
                val lastPlayer = remainingUnfinished.first()
                val lastRank = newWinners.size + 1
                val rankedLastPlayer = lastPlayer.copy(rank = lastRank)
                updatedPlayers = updatedPlayers.map {
                    if (it.seatIndex == lastPlayer.seatIndex) rankedLastPlayer else it
                }
                newWinners = newWinners + lastPlayer.color
            }

            events.add(
                LudoEvent.MatchCompleted(
                    standings = newWinners,
                    reason = "ALL_PLAYERS_FINISHED"
                )
            )

            val finalState = state.copy(
                players = updatedPlayers,
                currentRoll = null,
                phase = LudoTurnPhase.GAME_OVER,
                winners = newWinners,
                isGameOver = true
            )
            return finalState to events
        }

        // 6. Bonus roll evaluation
        val reachedGoal = selectedMove.to is LudoPosition.Finished
        val earnedBonus = roll == 6 || isCapture || reachedGoal

        if (hasPlayerFinished) {
            // Player just finished but game continues for other players: turn must pass!
            val intermediateState = state.copy(
                players = updatedPlayers,
                currentRoll = null,
                winners = newWinners
            )
            return passTurnToNextPlayer(intermediateState, events)
        }

        if (earnedBonus) {
            val bonusReason = when {
                reachedGoal -> BonusRollReason.REACHED_GOAL
                isCapture -> BonusRollReason.CAPTURED_OPPONENT
                else -> BonusRollReason.ROLLED_SIX
            }
            events.add(
                LudoEvent.BonusRollGranted(
                    playerColor = activePlayer.color,
                    seatIndex = activePlayer.seatIndex,
                    reason = bonusReason
                )
            )

            val nextState = state.copy(
                players = updatedPlayers,
                currentRoll = null,
                phase = LudoTurnPhase.WAITING_FOR_ROLL,
                winners = newWinners
            )
            return nextState to events
        }

        // 7. No bonus roll -> advance turn to next player
        val intermediateState = state.copy(
            players = updatedPlayers,
            currentRoll = null,
            winners = newWinners
        )
        return passTurnToNextPlayer(intermediateState, events)
    }

    private fun passTurnToNextPlayer(
        state: LudoGameState,
        accumulatedEvents: List<LudoEvent>
    ): Pair<LudoGameState, List<LudoEvent>> {
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

        // Check Anti-Stall Round Cutoff
        if (newRoundCount > state.maxRounds) {
            val unfinished = state.players.filterNot { it.isFinished }
            val rankedUnfinished = unfinished.sortedWith(
                compareByDescending<LudoPlayerState> { it.finishedTokenCount }
                    .thenByDescending { it.tokens.count { t -> t.position is LudoPosition.InHomePath } }
                    .thenBy { it.tokens.sumOf { t -> LudoBoard.distanceToGoal(t.position, t.color) } }
            )

            val allStandings = state.winners + rankedUnfinished.map { it.color }
            events.add(
                LudoEvent.MatchCompleted(
                    standings = allStandings,
                    reason = "TIME_LIMIT_REACHED"
                )
            )

            val finalState = state.copy(
                roundCount = state.maxRounds,
                phase = LudoTurnPhase.GAME_OVER,
                isGameOver = true,
                winners = allStandings
            )
            return finalState to events
        }

        val nextPlayer = state.players[nextIndex]
        events.add(
            LudoEvent.TurnPassed(
                previousSeatIndex = state.activeSeatIndex,
                nextSeatIndex = nextPlayer.seatIndex,
                nextPlayerColor = nextPlayer.color,
                roundCount = newRoundCount
            )
        )

        // Reset consecutive sixes for active player as their turn concludes
        val updatedPlayers = state.players.map { player ->
            if (player.seatIndex == state.activeSeatIndex) {
                player.copy(consecutiveSixes = 0)
            } else player
        }

        val nextState = state.copy(
            players = updatedPlayers,
            activeSeatIndex = nextPlayer.seatIndex,
            currentRoll = null,
            phase = LudoTurnPhase.WAITING_FOR_ROLL,
            roundCount = newRoundCount
        )

        return nextState to events
    }

    private fun LudoGameState.updatePlayer(updated: LudoPlayerState): LudoGameState =
        copy(players = players.map { if (it.seatIndex == updated.seatIndex) updated else it })
}
