package com.asadfareed.vaulta.domain.usecase

import com.asadfareed.vaulta.domain.model.Category
import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.model.MinorUnits
import com.asadfareed.vaulta.domain.model.NewTransaction
import com.asadfareed.vaulta.domain.repository.WalletRepository
import java.time.Clock
import javax.inject.Inject

sealed interface TransferResult {
    data class Success(val amount: MinorUnits, val recipient: Contact) : TransferResult
    sealed interface Failure : TransferResult {
        data object InvalidAmount : Failure
        data object ExceedsDailyLimit : Failure
        data class InsufficientFunds(val available: MinorUnits) : Failure
    }
}

class SendMoneyUseCase @Inject constructor(
    private val repository: WalletRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(
        recipient: Contact,
        amount: MinorUnits,
        note: String = "",
    ): TransferResult {
        if (amount <= 0) return TransferResult.Failure.InvalidAmount
        if (amount > DAILY_LIMIT) return TransferResult.Failure.ExceedsDailyLimit

        val available = repository.currentBalance()
        if (amount > available) return TransferResult.Failure.InsufficientFunds(available)

        repository.addTransaction(
            NewTransaction(
                title = recipient.name,
                subtitle = note.ifBlank { "Transfer to ${recipient.handle}" },
                amount = -amount,
                category = Category.TRANSFER,
                timestamp = clock.instant(),
            )
        )
        return TransferResult.Success(amount, recipient)
    }

    companion object {
        /** $5,000.00 per single transfer. */
        const val DAILY_LIMIT: MinorUnits = 500_000
    }
}
