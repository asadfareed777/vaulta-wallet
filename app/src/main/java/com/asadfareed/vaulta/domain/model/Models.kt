package com.asadfareed.vaulta.domain.model

import java.time.Instant

/** All money is kept in minor units (cents) to avoid floating-point rounding errors. */
typealias MinorUnits = Long

enum class Category(val label: String) {
    SALARY("Salary"),
    TRANSFER("Transfer"),
    SHOPPING("Shopping"),
    FOOD("Food & Dining"),
    TRANSPORT("Transport"),
    BILLS("Bills & Utilities"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health"),
}

data class Transaction(
    val id: Long,
    val title: String,
    val subtitle: String,
    /** Positive = money in, negative = money out. */
    val amount: MinorUnits,
    val category: Category,
    val timestamp: Instant,
) {
    val isIncome: Boolean get() = amount > 0
}

data class NewTransaction(
    val title: String,
    val subtitle: String,
    val amount: MinorUnits,
    val category: Category,
    val timestamp: Instant,
)

data class Contact(
    val id: String,
    val name: String,
    val handle: String,
)

enum class CardNetwork { VISA, MASTERCARD }

data class PaymentCard(
    val id: String,
    val holder: String,
    val last4: String,
    val expiry: String,
    val network: CardNetwork,
    val isVirtual: Boolean,
    val monthlyLimit: MinorUnits,
    val spentThisMonth: MinorUnits,
    val isFrozen: Boolean = false,
)

data class CategorySpend(
    val category: Category,
    val amount: MinorUnits,
    /** 0f..1f share of total spend in the period. */
    val share: Float,
)

data class DailySpend(
    val dayLabel: String,
    val amount: MinorUnits,
)

data class SpendingInsights(
    val totalSpent: MinorUnits,
    val byCategory: List<CategorySpend>,
    val lastSevenDays: List<DailySpend>,
    val changeVsPreviousPeriod: Float,
)

data class Dashboard(
    val balance: MinorUnits,
    val monthIncome: MinorUnits,
    val monthExpense: MinorUnits,
    val recent: List<Transaction>,
)
