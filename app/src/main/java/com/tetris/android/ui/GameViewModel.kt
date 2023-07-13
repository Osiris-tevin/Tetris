package com.tetris.android.ui

import androidx.lifecycle.ViewModel
import com.tetris.android.logic.event.GameEvent
import com.tetris.android.logic.state.GameViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _gameViewState = MutableStateFlow(GameViewState())

    val gameViewState = _gameViewState.asStateFlow()

    fun dispatch(event: GameEvent) =
        reduce(gameViewState.value, event)

    private fun reduce(state: GameViewState, event: GameEvent) {

    }

}