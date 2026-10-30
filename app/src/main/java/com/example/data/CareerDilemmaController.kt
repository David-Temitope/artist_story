package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

object CareerDilemmaController {
    private val _activeDilemma = MutableStateFlow<CareerDilemma?>(null)
    val activeDilemma: StateFlow<CareerDilemma?> = _activeDilemma.asStateFlow()

    fun triggerDilemma(state: GameStateEntity, customDilemma: CareerDilemma? = null) {
        _activeDilemma.value = customDilemma ?: CareerDilemmaGenerator.generateDilemmaForState(state)
    }

    fun maybeTriggerRandomDilemma(state: GameStateEntity) {
        // Use a life-week seed so reloads and bug reports can reproduce the same event.
        val seed = "${state.email}:${state.currentYear}:${state.currentWeek}".hashCode().toLong()
        val random = Random(seed)
        val financialPressure = if (state.cash < 1_000.0) 0.08f else 0f
        val wellbeingPressure = (state.stress - 60f).coerceAtLeast(0f) / 400f +
            (state.burnout - 50f).coerceAtLeast(0f) / 500f
        val eventChance = (0.22f + financialPressure + wellbeingPressure).coerceIn(0.18f, 0.45f)
        if (random.nextFloat() < eventChance) {
            _activeDilemma.value = CareerDilemmaGenerator.generateDilemmaForState(state, random)
        }
    }

    fun dismissDilemma() {
        _activeDilemma.value = null
    }

    fun resolveDilemma(choice: DilemmaChoice, viewModel: GameViewModel) {
        val currentDilemma = _activeDilemma.value
        val player = viewModel.gameState.value ?: return
        if (!choice.isAvailableFor(player.businessSkill, player.integrity)) {
            viewModel.showToast("You don't yet meet the requirements for that choice.")
            return
        }

        viewModel.updateState { s ->
            val updatedCash = s.cash + choice.cashDelta
            val updatedFans = (s.fans + choice.fansDelta).coerceAtLeast(0L)
            val updatedRep = (s.businessReputation + choice.reputationDelta).coerceIn(0f, 100f)
            val updatedStress = (s.stress + choice.stressDelta).coerceIn(0f, 100f)
            val updatedEnergy = (s.energy + choice.energyDelta).coerceIn(0, 100)

            val logEntry = "Year ${s.currentYear}, Week ${s.currentWeek}: [${currentDilemma?.category ?: "DECISION"}] Choice: \"${choice.actionText}\" -> ${choice.outcomeNarrative}"
            val newTimeline = s.timelineEvents + logEntry

            s.copy(
                cash = updatedCash,
                fans = updatedFans,
                businessReputation = updatedRep,
                stress = updatedStress,
                energy = updatedEnergy,
                timelineEvents = newTimeline
            )
        }

        // Post action feedback toast & stat deltas
        val feedbackDeltas = mutableListOf<StatFeedbackItem>()
        if (choice.cashDelta != 0.0) {
            feedbackDeltas.add(
                StatFeedbackItem(
                    label = "Cash",
                    deltaText = if (choice.cashDelta > 0) "+$${String.format("%,.0f", choice.cashDelta)}" else "-$${String.format("%,.0f", -choice.cashDelta)}",
                    isPositive = choice.cashDelta > 0
                )
            )
        }
        if (choice.fansDelta != 0L) {
            feedbackDeltas.add(
                StatFeedbackItem(
                    label = "Fans",
                    deltaText = if (choice.fansDelta > 0) "+${String.format("%,d", choice.fansDelta)}" else "${String.format("%,d", choice.fansDelta)}",
                    isPositive = choice.fansDelta > 0
                )
            )
        }
        if (choice.reputationDelta != 0f) {
            feedbackDeltas.add(
                StatFeedbackItem(
                    label = "Reputation",
                    deltaText = if (choice.reputationDelta > 0) "+${choice.reputationDelta.toInt()}%" else "${choice.reputationDelta.toInt()}%",
                    isPositive = choice.reputationDelta > 0
                )
            )
        }
        if (choice.stressDelta != 0f) {
            feedbackDeltas.add(
                StatFeedbackItem(
                    label = "Stress",
                    deltaText = if (choice.stressDelta > 0) "+${choice.stressDelta.toInt()}%" else "${choice.stressDelta.toInt()}%",
                    isPositive = choice.stressDelta <= 0
                )
            )
        }

        viewModel.postActionFeedback(
            title = choice.shortTitle,
            subtitle = choice.outcomeNarrative,
            deltas = feedbackDeltas,
            iconType = "star"
        )

        _activeDilemma.value = null
    }
}
