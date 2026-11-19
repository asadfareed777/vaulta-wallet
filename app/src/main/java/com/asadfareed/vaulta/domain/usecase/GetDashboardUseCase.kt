package com.asadfareed.vaulta.domain.usecase

import com.asadfareed.vaulta.domain.model.Dashboard
import com.asadfareed.vaulta.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetDashboardUseCase @Inject constructor(
    private val repository: WalletRepository,
    private val clock: Clock,
) {
    operator fun invoke(recentCount: Int = 6): Flow<Dashboard> =
        combine(repository.observeBalance(), repository.observeTransactions()) { balance, transactions ->
            val zone: ZoneId = clock.zone
            val monthStart = LocalDate.now(clock).withDayOfMonth(1).atStartOfDay(zone).toInstant()
            val thisMonth = transactions.filter { !it.timestamp.isBefore(monthStart) }
            Dashboard(
                balance = balance,
                monthIncome = thisMonth.filter { it.isIncome }.sumOf { it.amount },
                monthExpense = -thisMonth.filter { !it.isIncome }.sumOf { it.amount },
                recent = transactions.take(recentCount),
            )
        }
}
