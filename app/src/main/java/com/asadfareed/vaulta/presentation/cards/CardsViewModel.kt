package com.asadfareed.vaulta.presentation.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asadfareed.vaulta.domain.model.PaymentCard
import com.asadfareed.vaulta.domain.usecase.ObserveCardsUseCase
import com.asadfareed.vaulta.domain.usecase.ToggleCardFreezeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    observeCards: ObserveCardsUseCase,
    private val toggleFreeze: ToggleCardFreezeUseCase,
) : ViewModel() {

    val cards: StateFlow<List<PaymentCard>> = observeCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onToggleFreeze(card: PaymentCard) {
        viewModelScope.launch { toggleFreeze(card) }
    }
}
