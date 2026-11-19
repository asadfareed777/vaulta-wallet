package com.asadfareed.vaulta.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CallReceived
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asadfareed.vaulta.core.designsystem.VaultaColors
import com.asadfareed.vaulta.core.util.formatMoney
import com.asadfareed.vaulta.domain.model.Dashboard
import com.asadfareed.vaulta.presentation.components.Avatar
import com.asadfareed.vaulta.presentation.components.SectionHeader
import com.asadfareed.vaulta.presentation.components.TransactionRow

@Composable
fun HomeRoute(
    onSendMoney: () -> Unit,
    onSeeInsights: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onToggleBalance = viewModel::toggleBalanceVisibility,
        onSendMoney = onSendMoney,
        onSeeInsights = onSeeInsights,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onToggleBalance: () -> Unit,
    onSendMoney: () -> Unit,
    onSeeInsights: () -> Unit,
) {
    val dashboard = state.dashboard
    if (state.isLoading || dashboard == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = VaultaColors.Mint)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item { Greeting(state.userName) }
        item { Spacer(Modifier.height(20.dp)) }
        item {
            BalanceCard(
                dashboard = dashboard,
                hidden = state.balanceHidden,
                onToggle = onToggleBalance,
            )
        }
        item { Spacer(Modifier.height(24.dp)) }
        item { QuickActions(onSendMoney = onSendMoney) }
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader("Recent activity", action = "See all", onAction = onSeeInsights) }
        items(dashboard.recent, key = { it.id }) { TransactionRow(it) }
    }
}

@Composable
private fun Greeting(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(name = "Asad Fareed", color = VaultaColors.Mint, size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Welcome back,", style = MaterialTheme.typography.bodyMedium, color = VaultaColors.TextSecondary)
            Text(name, style = MaterialTheme.typography.titleLarge)
        }
        IconButton(
            onClick = {},
            modifier = Modifier
                .clip(CircleShape)
                .background(VaultaColors.Surface),
        ) {
            Icon(Icons.Rounded.NotificationsNone, contentDescription = "Notifications")
        }
    }
}

@Composable
private fun BalanceCard(dashboard: Dashboard, hidden: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(VaultaColors.CardGradient)
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Total balance", color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = if (hidden) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                contentDescription = "Toggle balance",
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onToggle),
            )
        }
        Spacer(Modifier.height(8.dp))
        AnimatedContent(targetState = hidden, label = "balance") { isHidden ->
            Text(
                text = if (isHidden) "$ ••••••" else dashboard.balance.formatMoney(),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MiniStat(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.ArrowDownward,
                label = "Income",
                value = dashboard.monthIncome.formatMoney(),
                tint = VaultaColors.Mint,
            )
            MiniStat(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.ArrowUpward,
                label = "Spent",
                value = dashboard.monthExpense.formatMoney(),
                tint = VaultaColors.Coral,
            )
        }
    }
}

@Composable
private fun MiniStat(modifier: Modifier, icon: ImageVector, label: String, value: String, tint: Color) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
            Text(value, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun QuickActions(onSendMoney: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        QuickAction(Icons.AutoMirrored.Rounded.Send, "Send", VaultaColors.Mint, onSendMoney)
        QuickAction(Icons.Rounded.CallReceived, "Request", VaultaColors.Violet) {}
        QuickAction(Icons.Rounded.Add, "Top up", VaultaColors.Amber) {}
        QuickAction(Icons.Rounded.QrCodeScanner, "Scan", VaultaColors.Sky) {}
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(VaultaColors.Surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = tint)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = VaultaColors.TextSecondary)
    }
}
