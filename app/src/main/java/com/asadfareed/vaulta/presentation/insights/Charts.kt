package com.asadfareed.vaulta.presentation.insights

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.asadfareed.vaulta.core.designsystem.VaultaColors
import com.asadfareed.vaulta.domain.model.CategorySpend
import com.asadfareed.vaulta.domain.model.DailySpend
import com.asadfareed.vaulta.presentation.components.tint

/** Animated rounded bar chart for daily spend. The highest bar is highlighted. */
@Composable
fun WeeklyBarChart(days: List<DailySpend>, modifier: Modifier = Modifier, chartHeight: Dp = 150.dp) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(days) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 700, easing = FastOutSlowInEasing))
    }
    val max = (days.maxOfOrNull { it.amount } ?: 0L).coerceAtLeast(1L)
    val peakIndex = days.indexOfFirst { it.amount == max }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        days.forEachIndexed { index, day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.height(chartHeight).width(28.dp), contentAlignment = Alignment.BottomCenter) {
                    Canvas(Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = VaultaColors.SurfaceHigh,
                            cornerRadius = CornerRadius(10.dp.toPx()),
                        )
                        val fraction = (day.amount.toFloat() / max) * progress.value
                        val barHeight = size.height * fraction
                        if (barHeight > 0f) {
                            drawRoundRect(
                                brush = if (index == peakIndex) {
                                    Brush.verticalGradient(listOf(VaultaColors.Mint, VaultaColors.Sky))
                                } else {
                                    Brush.verticalGradient(listOf(VaultaColors.Violet, VaultaColors.Violet.copy(alpha = 0.6f)))
                                },
                                topLeft = Offset(0f, size.height - barHeight),
                                size = Size(size.width, barHeight),
                                cornerRadius = CornerRadius(10.dp.toPx()),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(day.dayLabel, style = MaterialTheme.typography.labelMedium, color = VaultaColors.TextSecondary)
            }
        }
    }
}

/** Animated donut chart with a centred label slot. */
@Composable
fun SpendingDonut(
    segments: List<CategorySpend>,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 22.dp,
    center: @Composable ColumnScope.() -> Unit,
) {
    val sweep = remember { Animatable(0f) }
    LaunchedEffect(segments) {
        sweep.snapTo(0f)
        sweep.animateTo(1f, tween(durationMillis = 900, easing = FastOutSlowInEasing))
    }
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)
            drawArc(
                color = VaultaColors.SurfaceHigh,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke),
            )
            val gap = if (segments.size > 1) 4f else 0f
            var start = -90f
            segments.forEach { segment ->
                val angle = 360f * segment.share * sweep.value
                if (angle > gap) {
                    drawArc(
                        color = segment.category.tint,
                        startAngle = start + gap / 2,
                        sweepAngle = angle - gap,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round),
                    )
                }
                start += angle
            }
        }
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = center,
        )
    }
}
