package com.asadfareed.vaulta.presentation.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asadfareed.vaulta.core.util.formatMoney
import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.usecase.GetContactsUseCase
import com.asadfareed.vaulta.domain.usecase.SendMoneyUseCase
import com.asadfareed.vaulta.domain.usecase.TransferResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransferUiState(
    val contacts: List<Contact> = emptyList(),
    val selected: Contact? = null,
    /** Raw keypad input, e.g. "125.5". */
    val amountInput: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    val completed: TransferResult.Success? = null,
) {
    val amountMinor: Long
        get() = amountInput.toBigDecimalOrNull()?.movePointRight(2)?.toLong() ?: 0L

    val canSend: Boolean get() = selected != null && amountMinor > 0 && !isSending
}

sealed interface KeypadKey {
    data class Digit(val value: Char) : KeypadKey
    data object Decimal : KeypadKey
    data object Delete : KeypadKey
}

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val getContacts: GetContactsUseCase,
    private val sendMoney: SendMoneyUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TransferUiState())
    val state: StateFlow<TransferUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val contacts = getContacts()
            _state.update { it.copy(contacts = contacts, selected = contacts.firstOrNull()) }
        }
    }

    fun select(contact: Contact) = _state.update { it.copy(selected = contact, error = null) }

    fun onKey(key: KeypadKey) = _state.update { s ->
        val current = s.amountInput
        val next = when (key) {
            KeypadKey.Delete -> current.dropLast(1)
            KeypadKey.Decimal -> if ('.' in current) current else current.ifEmpty { "0" } + "."
            is KeypadKey.Digit -> {
                val decimals = current.substringAfter('.', missingDelimiterValue = "")
                when {
                    '.' in current && decimals.length >= 2 -> current
                    current == "0" -> key.value.toString()
                    current.substringBefore('.').length >= 6 && '.' !in current -> current
                    else -> current + key.value
                }
            }
        }
        s.copy(amountInput = next, error = null)
    }

    fun send() {
        val s = _state.value
        val recipient = s.selected ?: return
        if (!s.canSend) return
        viewModelScope.launch {
            _state.update { it.copy(isSending = true, error = null) }
            val result = sendMoney(recipient, s.amountMinor)
            _state.update {
                when (result) {
                    is TransferResult.Success -> it.copy(isSending = false, completed = result)
                    TransferResult.Failure.InvalidAmount ->
                        it.copy(isSending = false, error = "Enter a valid amount")
                    TransferResult.Failure.ExceedsDailyLimit ->
                        it.copy(isSending = false, error = "Max ${SendMoneyUseCase.DAILY_LIMIT.formatMoney()} per transfer")
                    is TransferResult.Failure.InsufficientFunds ->
                        it.copy(isSending = false, error = "Insufficient funds · ${result.available.formatMoney()} available")
                }
            }
        }
    }

    fun reset() = _state.update { it.copy(amountInput = "", completed = null, error = null) }
}
