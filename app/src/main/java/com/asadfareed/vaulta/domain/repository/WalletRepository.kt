package com.asadfareed.vaulta.domain.repository

import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.model.MinorUnits
import com.asadfareed.vaulta.domain.model.NewTransaction
import com.asadfareed.vaulta.domain.model.PaymentCard
import com.asadfareed.vaulta.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    fun observeBalance(): Flow<MinorUnits>
    fun observeCards(): Flow<List<PaymentCard>>
    suspend fun currentBalance(): MinorUnits
    suspend fun contacts(): List<Contact>
    suspend fun addTransaction(transaction: NewTransaction)
    suspend fun setCardFrozen(cardId: String, frozen: Boolean)
}
