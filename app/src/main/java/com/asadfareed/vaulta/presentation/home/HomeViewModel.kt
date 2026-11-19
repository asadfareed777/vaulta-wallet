package com.asadfareed.vaulta.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asadfareed.vaulta.domain.model.Dashboard
import com.asadfareed.vaulta.domain.usecase.GetDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "Asad",
    val dashboard: Dashboard? = null,
    val balanceHidden: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getDashboard: GetDashboardUseCase,
) : ViewModel() {

    private val balanceHidden = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> =
        combine(getDashboard(), balanceHidden) { dashboard, hidden ->
            HomeUiState(isLoading = false, dashboard = dashboard, balanceHidden = hidden)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    fun toggleBalanceVisibility() = balanceHidden.update { !it }
}
