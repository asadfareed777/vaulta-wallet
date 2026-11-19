package com.asadfareed.vaulta.presentation.insights

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asadfareed.vaulta.core.designsystem.VaultaColors
import com.asadfareed.vaulta.core.util.formatMoney
import com.asadfareed.vaulta.domain.model.CategorySpend
import com.asadfareed.vaulta.domain.model.SpendingInsights
import com.asadfareed.vaulta.presentation.components.CategoryBadge
import com.asadfareed.vaulta.presentation.components.tint
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun InsightsRoute(viewModel: InsightsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        InsightsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = VaultaColors.Mint)
        }
        is InsightsUiState.Ready -> InsightsScreen(s.insights)
    }
}

@Composable
fun InsightsScreen(insights: SpendingInsights) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text("Insights", style = MaterialTheme.typography.headlineSmall)
                Text("Last 30 days", color = VaultaColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item { SummaryCard(insights) }
        item {
            Panel {
                Text("This week", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                WeeklyBarChart(insights.lastSevenDays)
            }
        }
        item {
            Panel {
                Text("Where your money went", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(20.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    SpendingDonut(segments = insights.byCategory) {
                        Text("Spent", color = VaultaColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
                        Text(insights.totalSpent.formatMoney(), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
        items(insights.byCategory, key = { it.category }) { CategoryRow(it) }
    }
}

@Composable
private fun SummaryCard(insights: SpendingInsights) {
    val up = insights.changeVsPreviousPeriod > 0
    val pct = (abs(insights.changeVsPreviousPeriod) * 100).roundToInt()
    Panel {
        Text("Total spent", color = VaultaColors.TextSecondary, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(insights.totalSpent.formatMoney(), style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.width(12.dp))
            val tint = if (up) VaultaColors.Coral else VaultaColors.Mint
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(tint.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    if (up) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown,
                    contentDescription = null,
                    tint = tint,
                )
                Spacer(Modifier.width(4.dp))
                Text("$pct%", color = tint, style = MaterialTheme.typography.labelLarge)
            }
        }
        Text(
            "vs. previous 30 days",
            color = VaultaColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun CategoryRow(item: CategorySpend) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CategoryBadge(item.category)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Row {
                Text(item.category.label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(item.amount.formatMoney(), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { item.share },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = item.category.tint,
                trackColor = VaultaColors.SurfaceHigh,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
        }
    }
}

@Composable
private fun Panel(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(VaultaColors.Surface)
            .padding(20.dp),
    ) { content() }
}
