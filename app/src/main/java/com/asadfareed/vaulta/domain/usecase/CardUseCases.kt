package com.asadfareed.vaulta.domain.usecase

import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.model.PaymentCard
import com.asadfareed.vaulta.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCardsUseCase @Inject constructor(
    private val repository: WalletRepository,
) {
    operator fun invoke(): Flow<List<PaymentCard>> = repository.observeCards()
}

class ToggleCardFreezeUseCase @Inject constructor(
    private val repository: WalletRepository,
) {
    suspend operator fun invoke(card: PaymentCard) =
        repository.setCardFrozen(card.id, !card.isFrozen)
}

class GetContactsUseCase @Inject constructor(
    private val repository: WalletRepository,
) {
    suspend operator fun invoke(): List<Contact> = repository.contacts()
}
