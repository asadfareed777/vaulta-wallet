package com.asadfareed.vaulta.data.mapper

import com.asadfareed.vaulta.data.local.TransactionEntity
import com.asadfareed.vaulta.domain.model.Category
import com.asadfareed.vaulta.domain.model.NewTransaction
import com.asadfareed.vaulta.domain.model.Transaction
import java.time.Instant

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    title = title,
    subtitle = subtitle,
    amount = amountMinor,
    category = runCatching { Category.valueOf(category) }.getOrDefault(Category.TRANSFER),
    timestamp = Instant.ofEpochMilli(timestamp),
)

fun NewTransaction.toEntity(): TransactionEntity = TransactionEntity(
    title = title,
    subtitle = subtitle,
    amountMinor = amount,
    category = category.name,
    timestamp = timestamp.toEpochMilli(),
)
