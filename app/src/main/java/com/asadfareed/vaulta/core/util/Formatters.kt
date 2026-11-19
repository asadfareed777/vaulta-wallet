package com.asadfareed.vaulta.core.util

import com.asadfareed.vaulta.domain.model.MinorUnits
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

private val currencyFormat: NumberFormat =
    NumberFormat.getCurrencyInstance(Locale.US).apply { currency = Currency.getInstance("USD") }

fun MinorUnits.formatMoney(): String = currencyFormat.format(this / 100.0)

fun MinorUnits.formatSigned(): String =
    (if (this >= 0) "+" else "−") + abs(this).formatMoney()

private val dateFormat = DateTimeFormatter.ofPattern("d MMM", Locale.US)

fun Instant.relativeLabel(now: Instant = Instant.now()): String {
    val diff = Duration.between(this, now)
    return when {
        diff.toMinutes() < 1 -> "Just now"
        diff.toHours() < 1 -> "${diff.toMinutes()} min ago"
        diff.toHours() < 24 -> "${diff.toHours()} h ago"
        diff.toDays() < 2 -> "Yesterday"
        diff.toDays() < 7 -> "${diff.toDays()} days ago"
        else -> dateFormat.format(atZone(ZoneId.systemDefault()))
    }
}

fun String.initials(): String =
    split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
