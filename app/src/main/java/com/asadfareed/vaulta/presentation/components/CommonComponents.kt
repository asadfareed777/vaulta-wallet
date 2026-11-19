package com.asadfareed.vaulta.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.asadfareed.vaulta.core.designsystem.VaultaColors
import com.asadfareed.vaulta.core.util.formatSigned
import com.asadfareed.vaulta.core.util.initials
import com.asadfareed.vaulta.core.util.relativeLabel
import com.asadfareed.vaulta.domain.model.Category
import com.asadfareed.vaulta.domain.model.Transaction

val Category.icon: ImageVector
    get() = when (this) {
        Category.SALARY -> Icons.Rounded.Payments
        Category.TRANSFER -> Icons.Rounded.SwapHoriz
        Category.SHOPPING -> Icons.Rounded.ShoppingBag
        Category.FOOD -> Icons.Rounded.Restaurant
        Category.TRANSPORT -> Icons.Rounded.DirectionsCar
        Category.BILLS -> Icons.AutoMirrored.Rounded.ReceiptLong
        Category.ENTERTAINMENT -> Icons.Rounded.Movie
        Category.HEALTH -> Icons.Rounded.LocalHospital
    }

val Category.tint: Color
    get() = when (this) {
        Category.SALARY -> VaultaColors.Mint
        Category.TRANSFER -> VaultaColors.Sky
        Category.SHOPPING -> VaultaColors.Violet
        Category.FOOD -> VaultaColors.Amber
        Category.TRANSPORT -> VaultaColors.Sky
        Category.BILLS -> VaultaColors.Coral
        Category.ENTERTAINMENT -> VaultaColors.Pink
        Category.HEALTH -> VaultaColors.Mint
    }

@Composable
fun CategoryBadge(category: Category, size: Dp = 44.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(category.tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(category.icon, contentDescription = category.label, tint = category.tint)
    }
}

@Composable
fun Avatar(name: String, color: Color, size: Dp = 48.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(name.initials(), color = color, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun TransactionRow(transaction: Transaction, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryBadge(transaction.category)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                transaction.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                transaction.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = VaultaColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                transaction.amount.formatSigned(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.isIncome) VaultaColors.Mint else VaultaColors.TextPrimary,
            )
            Text(
                transaction.timestamp.relativeLabel(),
                style = MaterialTheme.typography.labelMedium,
                color = VaultaColors.TextSecondary,
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (action != null) {
            androidx.compose.material3.TextButton(onClick = onAction) {
                Text(action, color = VaultaColors.Mint)
            }
        }
    }
}

val avatarPalette = listOf(
    VaultaColors.Mint, VaultaColors.Violet, VaultaColors.Amber,
    VaultaColors.Sky, VaultaColors.Pink, VaultaColors.Coral,
)
