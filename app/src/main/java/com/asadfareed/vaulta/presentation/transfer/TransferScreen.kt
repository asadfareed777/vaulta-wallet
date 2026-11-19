package com.asadfareed.vaulta.presentation.transfer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asadfareed.vaulta.core.designsystem.VaultaColors
import com.asadfareed.vaulta.core.util.formatMoney
import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.presentation.components.Avatar
import com.asadfareed.vaulta.presentation.components.avatarPalette

@Composable
fun TransferRoute(onBack: () -> Unit, viewModel: TransferViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TransferScreen(
        state = state,
        onBack = onBack,
        onSelect = viewModel::select,
        onKey = viewModel::onKey,
        onSend = viewModel::send,
        onDone = {
            viewModel.reset()
            onBack()
        },
    )
}

@Composable
fun TransferScreen(
    state: TransferUiState,
    onBack: () -> Unit,
    onSelect: (Contact) -> Unit,
    onKey: (KeypadKey) -> Unit,
    onSend: () -> Unit,
    onDone: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
                Text("Send money", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(16.dp))
            Text("To", color = VaultaColors.TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(10.dp))
            ContactPicker(state.contacts, state.selected, onSelect)

            Spacer(Modifier.weight(1f))
            AmountDisplay(state)
            Spacer(Modifier.weight(1f))

            Keypad(onKey)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onSend,
                enabled = state.canSend,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VaultaColors.Mint,
                    contentColor = VaultaColors.Ink,
                    disabledContainerColor = VaultaColors.SurfaceHigh,
                ),
            ) {
                if (state.isSending) {
                    CircularProgressIndicator(Modifier.size(22.dp), color = VaultaColors.Ink, strokeWidth = 2.dp)
                } else {
                    val who = state.selected?.name?.substringBefore(' ') ?: ""
                    Text("Send to $who", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        AnimatedVisibility(
            visible = state.completed != null,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
        ) {
            state.completed?.let { result ->
                SuccessOverlay(
                    amount = result.amount.formatMoney(),
                    recipient = result.recipient.name,
                    onDone = onDone,
                )
            }
        }
    }
}

@Composable
private fun ContactPicker(contacts: List<Contact>, selected: Contact?, onSelect: (Contact) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(end = 8.dp)) {
        itemsIndexed(contacts, key = { _, c -> c.id }) { index, contact ->
            val isSelected = contact.id == selected?.id
            val color = avatarPalette[index % avatarPalette.size]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelect(contact) }
                    .padding(4.dp),
            ) {
                Box(
                    Modifier
                        .border(
                            BorderStroke(2.dp, if (isSelected) VaultaColors.Mint else VaultaColors.Ink),
                            CircleShape,
                        )
                        .padding(4.dp),
                ) { Avatar(contact.name, color, size = 52.dp) }
                Spacer(Modifier.height(6.dp))
                Text(
                    contact.name.substringBefore(' '),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) VaultaColors.TextPrimary else VaultaColors.TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun AmountDisplay(state: TransferUiState) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$" + state.amountInput.ifEmpty { "0" },
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = if (state.amountInput.isEmpty()) VaultaColors.TextSecondary else VaultaColors.TextPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = state.error ?: "No fees on Vaulta-to-Vaulta transfers",
            color = if (state.error != null) VaultaColors.Coral else VaultaColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun Keypad(onKey: (KeypadKey) -> Unit) {
    val rows = listOf(
        listOf(KeypadKey.Digit('1'), KeypadKey.Digit('2'), KeypadKey.Digit('3')),
        listOf(KeypadKey.Digit('4'), KeypadKey.Digit('5'), KeypadKey.Digit('6')),
        listOf(KeypadKey.Digit('7'), KeypadKey.Digit('8'), KeypadKey.Digit('9')),
        listOf(KeypadKey.Decimal, KeypadKey.Digit('0'), KeypadKey.Delete),
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2.1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(VaultaColors.Surface)
                            .clickable { onKey(key) },
                        contentAlignment = Alignment.Center,
                    ) {
                        when (key) {
                            is KeypadKey.Digit -> Text(key.value.toString(), fontSize = 24.sp, fontWeight = FontWeight.Medium)
                            KeypadKey.Decimal -> Text(".", fontSize = 24.sp, fontWeight = FontWeight.Medium)
                            KeypadKey.Delete -> Icon(Icons.AutoMirrored.Rounded.Backspace, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessOverlay(amount: String, recipient: String, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VaultaColors.Ink)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(VaultaColors.Mint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = VaultaColors.Mint, modifier = Modifier.size(72.dp))
        }
        Spacer(Modifier.height(28.dp))
        Text("Transfer sent", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(amount, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("to $recipient", color = VaultaColors.TextSecondary)
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.width(220.dp).height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VaultaColors.Mint, contentColor = VaultaColors.Ink),
        ) { Text("Done", fontWeight = FontWeight.SemiBold) }
    }
}
