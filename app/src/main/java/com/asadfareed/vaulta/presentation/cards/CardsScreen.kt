package com.asadfareed.vaulta.presentation.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asadfareed.vaulta.core.designsystem.VaultaColors
import com.asadfareed.vaulta.core.util.formatMoney
import com.asadfareed.vaulta.domain.model.CardNetwork
import com.asadfareed.vaulta.domain.model.PaymentCard

@Composable
fun CardsRoute(viewModel: CardsViewModel = hiltViewModel()) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    CardsScreen(cards = cards, onToggleFreeze = viewModel::onToggleFreeze)
}

@Composable
fun CardsScreen(cards: List<PaymentCard>, onToggleFreeze: (PaymentCard) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { Text("My cards", style = MaterialTheme.typography.headlineSmall) }
        items(cards, key = { it.id }) { card ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PaymentCardView(card)
                CardControls(card, onToggleFreeze)
            }
        }
    }
}

@Composable
fun PaymentCardView(card: PaymentCard) {
    val brush = when {
        card.isFrozen -> VaultaColors.FrozenGradient
        card.isVirtual -> VaultaColors.VirtualCardGradient
        else -> VaultaColors.CardGradient
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f) // ISO/IEC 7810 ID-1 card ratio
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .padding(22.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("vaulta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.weight(1f))
            if (card.isVirtual) {
                Text(
                    "VIRTUAL",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Icon(Icons.Rounded.Contactless, contentDescription = null, tint = Color.White)
        }
        Text(
            "••••  ••••  ••••  ${card.last4}",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        Row(Modifier.align(Alignment.BottomStart).fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text("CARD HOLDER", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                Text(card.holder, color = Color.White, fontWeight = FontWeight.Medium)
            }
            Column(Modifier.padding(end = 16.dp)) {
                Text("EXPIRES", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                Text(card.expiry, color = Color.White, fontWeight = FontWeight.Medium)
            }
            NetworkMark(card.network)
        }
        if (card.isFrozen) {
            Icon(
                Icons.Rounded.Lock,
                contentDescription = "Frozen",
                tint = Color.White,
                modifier = Modifier.align(Alignment.TopCenter).size(32.dp),
            )
        }
    }
}

@Composable
private fun NetworkMark(network: CardNetwork) {
    when (network) {
        CardNetwork.VISA -> Text("VISA", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
        CardNetwork.MASTERCARD -> Row {
            Box(Modifier.size(28.dp).clip(RoundedCornerShape(50)).background(Color(0xFFEB001B)))
            Box(
                Modifier
                    .offset(x = (-10).dp)
                    .size(28.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF79E1B).copy(alpha = 0.9f))
            )
        }
    }
}

@Composable
private fun CardControls(card: PaymentCard, onToggleFreeze: (PaymentCard) -> Unit) {
    val usage = (card.spentThisMonth.toFloat() / card.monthlyLimit).coerceIn(0f, 1f)
    val tint by animateColorAsState(if (card.isFrozen) VaultaColors.Sky else VaultaColors.TextSecondary, label = "freeze")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(VaultaColors.Surface)
            .padding(18.dp),
    ) {
        Row {
            Text("Monthly limit", color = VaultaColors.TextSecondary, modifier = Modifier.weight(1f))
            Text("${card.spentThisMonth.formatMoney()} / ${card.monthlyLimit.formatMoney()}")
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { usage },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
            color = if (usage > 0.8f) VaultaColors.Coral else VaultaColors.Mint,
            trackColor = VaultaColors.SurfaceHigh,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.AcUnit, contentDescription = null, tint = tint)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Freeze card", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (card.isFrozen) "Payments are blocked" else "Temporarily block all payments",
                    color = VaultaColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(
                checked = card.isFrozen,
                onCheckedChange = { onToggleFreeze(card) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = VaultaColors.Ink,
                    checkedTrackColor = VaultaColors.Sky,
                ),
            )
        }
    }
}
