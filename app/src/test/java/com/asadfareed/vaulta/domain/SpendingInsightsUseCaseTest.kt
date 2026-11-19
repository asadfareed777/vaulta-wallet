package com.asadfareed.vaulta.domain

import com.asadfareed.vaulta.domain.model.Category
import com.asadfareed.vaulta.domain.model.Transaction
import com.asadfareed.vaulta.domain.usecase.GetDashboardUseCase
import com.asadfareed.vaulta.domain.usecase.GetSpendingInsightsUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class SpendingInsightsUseCaseTest {

    private val now = Instant.parse("2026-11-15T12:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)

    private fun tx(id: Long, amount: Long, category: Category, daysAgo: Long) = Transaction(
        id = id,
        title = "t$id",
        subtitle = "",
        amount = amount,
        category = category,
        timestamp = now.minus(Duration.ofDays(daysAgo)).minusSeconds(60),
    )

    private val data = listOf(
        tx(1, 500_000, Category.SALARY, 1),
        tx(2, -30_000, Category.FOOD, 0),
        tx(3, -10_000, Category.FOOD, 2),
        tx(4, -60_000, Category.SHOPPING, 5),
        // previous 30-day window
        tx(5, -50_000, Category.BILLS, 40),
    )

    @Test
    fun `groups expenses by category ordered by amount`() {
        val insights = GetSpendingInsightsUseCase(FakeWalletRepository(data), clock).compute(data, 30)

        assertThat(insights.totalSpent).isEqualTo(100_000L)
        assertThat(insights.byCategory.map { it.category })
            .containsExactly(Category.SHOPPING, Category.FOOD).inOrder()
        assertThat(insights.byCategory.first().share).isWithin(0.001f).of(0.6f)
    }

    @Test
    fun `computes change against the previous period`() {
        val insights = GetSpendingInsightsUseCase(FakeWalletRepository(data), clock).compute(data, 30)
        // 100k now vs 50k before = +100%
        assertThat(insights.changeVsPreviousPeriod).isWithin(0.001f).of(1f)
    }

    @Test
    fun `last seven days always has seven entries ending today`() {
        val insights = GetSpendingInsightsUseCase(FakeWalletRepository(data), clock).compute(data, 30)

        assertThat(insights.lastSevenDays).hasSize(7)
        assertThat(insights.lastSevenDays.last().amount).isEqualTo(30_000L)
    }

    @Test
    fun `dashboard sums only the current month`() = runTest {
        val dashboard = GetDashboardUseCase(FakeWalletRepository(data), clock)().first()

        assertThat(dashboard.balance).isEqualTo(350_000L)
        assertThat(dashboard.monthIncome).isEqualTo(500_000L)
        assertThat(dashboard.monthExpense).isEqualTo(100_000L)
        assertThat(dashboard.recent.first().id).isEqualTo(2L)
    }
}
