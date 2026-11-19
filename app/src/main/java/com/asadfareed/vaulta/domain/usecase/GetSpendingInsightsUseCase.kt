package com.asadfareed.vaulta.domain.usecase

import com.asadfareed.vaulta.domain.model.CategorySpend
import com.asadfareed.vaulta.domain.model.DailySpend
import com.asadfareed.vaulta.domain.model.SpendingInsights
import com.asadfareed.vaulta.domain.model.Transaction
import com.asadfareed.vaulta.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

/**
 * Builds spending analytics for the last [periodDays] days and compares
 * them with the period immediately before it.
 */
class GetSpendingInsightsUseCase @Inject constructor(
    private val repository: WalletRepository,
    private val clock: Clock,
) {
    operator fun invoke(periodDays: Long = 30): Flow<SpendingInsights> =
        repository.observeTransactions().map { compute(it, periodDays) }

    internal fun compute(transactions: List<Transaction>, periodDays: Long): SpendingInsights {
        val now = clock.instant()
        val periodStart = now.minus(periodDays, ChronoUnit.DAYS)
        val previousStart = periodStart.minus(periodDays, ChronoUnit.DAYS)

        val expenses = transactions.filter { !it.isIncome }
        val current = expenses.filter { it.timestamp.isAfter(periodStart) }
        val previous = expenses.filter {
            it.timestamp.isAfter(previousStart) && !it.timestamp.isAfter(periodStart)
        }

        val total = -current.sumOf { it.amount }
        val previousTotal = -previous.sumOf { it.amount }

        val byCategory = current
            .groupBy { it.category }
            .map { (category, items) ->
                val spent = -items.sumOf { it.amount }
                CategorySpend(
                    category = category,
                    amount = spent,
                    share = if (total == 0L) 0f else spent.toFloat() / total,
                )
            }
            .sortedByDescending { it.amount }

        val today = LocalDate.now(clock)
        val zone = clock.zone
        val lastSevenDays = (6 downTo 0).map { offset ->
            val day = today.minusDays(offset.toLong())
            val spent = -expenses
                .filter { it.timestamp.atZone(zone).toLocalDate() == day }
                .sumOf { it.amount }
            DailySpend(
                dayLabel = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                amount = spent,
            )
        }

        val change = if (previousTotal == 0L) 0f
        else (total - previousTotal).toFloat() / previousTotal

        return SpendingInsights(total, byCategory, lastSevenDays, change)
    }
}
