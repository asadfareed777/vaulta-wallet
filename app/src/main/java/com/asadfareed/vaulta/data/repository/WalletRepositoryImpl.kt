package com.asadfareed.vaulta.data.repository

import com.asadfareed.vaulta.data.demo.DemoData
import com.asadfareed.vaulta.data.local.TransactionDao
import com.asadfareed.vaulta.data.mapper.toDomain
import com.asadfareed.vaulta.data.mapper.toEntity
import com.asadfareed.vaulta.di.IoDispatcher
import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.model.MinorUnits
import com.asadfareed.vaulta.domain.model.NewTransaction
import com.asadfareed.vaulta.domain.model.PaymentCard
import com.asadfareed.vaulta.domain.model.Transaction
import com.asadfareed.vaulta.domain.repository.WalletRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
    private val clock: Clock,
    @IoDispatcher private val io: CoroutineDispatcher,
) : WalletRepository {

    private val seedMutex = Mutex()
    private val cards = MutableStateFlow(DemoData.cards)

    override fun observeTransactions(): Flow<List<Transaction>> =
        dao.observeAll()
            .onStart { seedIfEmpty() }
            .map { list -> list.map { it.toDomain() } }
            .flowOn(io)

    override fun observeBalance(): Flow<MinorUnits> =
        dao.observeBalance()
            .onStart { seedIfEmpty() }
            .flowOn(io)

    override fun observeCards(): Flow<List<PaymentCard>> = cards.asStateFlow()

    override suspend fun currentBalance(): MinorUnits = withContext(io) { dao.balance() }

    override suspend fun contacts(): List<Contact> = DemoData.contacts

    override suspend fun addTransaction(transaction: NewTransaction) {
        withContext(io) { dao.insert(transaction.toEntity()) }
    }

    override suspend fun setCardFrozen(cardId: String, frozen: Boolean) {
        cards.update { list ->
            list.map { if (it.id == cardId) it.copy(isFrozen = frozen) else it }
        }
    }

    private suspend fun seedIfEmpty() {
        seedMutex.withLock {
            if (dao.count() == 0) {
                dao.insertAll(DemoData.transactions(clock).map { it.toEntity() })
            }
        }
    }
}
