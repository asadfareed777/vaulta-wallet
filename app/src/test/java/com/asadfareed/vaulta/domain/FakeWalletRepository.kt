package com.asadfareed.vaulta.domain

import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.model.MinorUnits
import com.asadfareed.vaulta.domain.model.NewTransaction
import com.asadfareed.vaulta.domain.model.PaymentCard
import com.asadfareed.vaulta.domain.model.Transaction
import com.asadfareed.vaulta.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeWalletRepository(initial: List<Transaction> = emptyList()) : WalletRepository {

    private val transactions = MutableStateFlow(initial)
    private val cards = MutableStateFlow<List<PaymentCard>>(emptyList())
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1

    val all: List<Transaction> get() = transactions.value

    override fun observeTransactions(): Flow<List<Transaction>> =
        transactions.map { list -> list.sortedByDescending { it.timestamp } }

    override fun observeBalance(): Flow<MinorUnits> = transactions.map { list -> list.sumOf { it.amount } }

    override fun observeCards(): Flow<List<PaymentCard>> = cards

    override suspend fun currentBalance(): MinorUnits = transactions.value.sumOf { it.amount }

    override suspend fun contacts(): List<Contact> = listOf(Contact("c1", "Sara Khan", "@sarak"))

    override suspend fun addTransaction(transaction: NewTransaction) {
        transactions.update {
            it + Transaction(
                id = nextId++,
                title = transaction.title,
                subtitle = transaction.subtitle,
                amount = transaction.amount,
                category = transaction.category,
                timestamp = transaction.timestamp,
            )
        }
    }

    override suspend fun setCardFrozen(cardId: String, frozen: Boolean) {
        cards.update { list -> list.map { if (it.id == cardId) it.copy(isFrozen = frozen) else it } }
    }
}
