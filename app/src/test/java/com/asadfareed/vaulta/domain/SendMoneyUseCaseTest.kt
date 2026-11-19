package com.asadfareed.vaulta.domain

import com.asadfareed.vaulta.domain.model.Category
import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.model.Transaction
import com.asadfareed.vaulta.domain.usecase.SendMoneyUseCase
import com.asadfareed.vaulta.domain.usecase.TransferResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SendMoneyUseCaseTest {

    private val now = Instant.parse("2026-11-15T10:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val sara = Contact("c1", "Sara Khan", "@sarak")

    private lateinit var repository: FakeWalletRepository
    private lateinit var sendMoney: SendMoneyUseCase

    @Before
    fun setUp() {
        repository = FakeWalletRepository(
            listOf(Transaction(1, "Salary", "", 100_000, Category.SALARY, now.minusSeconds(3_600)))
        )
        sendMoney = SendMoneyUseCase(repository, clock)
    }

    @Test
    fun `successful transfer debits the balance`() = runTest {
        val result = sendMoney(sara, 25_000)

        assertThat(result).isEqualTo(TransferResult.Success(25_000, sara))
        assertThat(repository.currentBalance()).isEqualTo(75_000L)
        assertThat(repository.all.last().category).isEqualTo(Category.TRANSFER)
        assertThat(repository.all.last().timestamp).isEqualTo(now)
    }

    @Test
    fun `zero or negative amount is rejected`() = runTest {
        assertThat(sendMoney(sara, 0)).isEqualTo(TransferResult.Failure.InvalidAmount)
        assertThat(sendMoney(sara, -10)).isEqualTo(TransferResult.Failure.InvalidAmount)
        assertThat(repository.all).hasSize(1)
    }

    @Test
    fun `amount above balance reports available funds`() = runTest {
        val result = sendMoney(sara, 100_001)

        assertThat(result).isEqualTo(TransferResult.Failure.InsufficientFunds(100_000))
        assertThat(repository.currentBalance()).isEqualTo(100_000L)
    }

    @Test
    fun `amount above per-transfer limit is rejected`() = runTest {
        val result = sendMoney(sara, SendMoneyUseCase.DAILY_LIMIT + 1)
        assertThat(result).isEqualTo(TransferResult.Failure.ExceedsDailyLimit)
    }

    @Test
    fun `blank note falls back to recipient handle`() = runTest {
        sendMoney(sara, 1_000, note = "  ")
        assertThat(repository.all.last().subtitle).isEqualTo("Transfer to @sarak")
    }
}
