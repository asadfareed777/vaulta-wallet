package com.asadfareed.vaulta.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asadfareed.vaulta.domain.model.SpendingInsights
import com.asadfareed.vaulta.domain.usecase.GetSpendingInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface InsightsUiState {
    data object Loading : InsightsUiState
    data class Ready(val insights: SpendingInsights) : InsightsUiState
}

@HiltViewModel
class InsightsViewModel @Inject constructor(
    getInsights: GetSpendingInsightsUseCase,
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = getInsights()
        .map<SpendingInsights, InsightsUiState> { InsightsUiState.Ready(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InsightsUiState.Loading)
}
