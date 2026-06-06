package com.sifip.cibademo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sifip.cibademo.ui.theme.AppBorder
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.AppText
import com.sifip.cibademo.ui.theme.StatusError
import com.sifip.cibademo.ui.theme.StatusOk

enum class CheckStatus { Idle, Running, Ok, Failed }

data class CheckRow(val label: String, val status: CheckStatus, val message: String? = null)

@Composable
fun CheckStepView(
    row: CheckRow,
    modifier: Modifier = Modifier,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Indicator(status = row.status, runningColor = indicatorColor)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.label,
                color = AppText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            if (row.message != null) {
                Text(
                    text = row.message,
                    color = when (row.status) {
                        CheckStatus.Ok -> StatusOk
                        CheckStatus.Failed -> StatusError
                        else -> AppMuted
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun Indicator(status: CheckStatus, runningColor: Color) {
    val size = 22.dp
    when (status) {
        CheckStatus.Idle -> Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(AppBorder),
        )
        CheckStatus.Running -> CircularProgressIndicator(
            modifier = Modifier.size(size),
            strokeWidth = 2.5.dp,
            color = runningColor,
        )
        CheckStatus.Ok -> Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(StatusOk),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "OK",
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
        }
        CheckStatus.Failed -> Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(StatusError),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Échec",
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}
