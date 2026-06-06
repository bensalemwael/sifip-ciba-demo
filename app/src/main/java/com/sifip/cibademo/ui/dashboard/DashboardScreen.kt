package com.sifip.cibademo.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.R
import com.sifip.cibademo.ui.components.BankTopBar
import com.sifip.cibademo.ui.theme.AppBorder
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.AppText
import com.sifip.cibademo.ui.theme.BankPurple
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onTransferClicked: () -> Unit,
    onLogout: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val account = state.account

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        BankTopBar(
            title = stringResource(R.string.dashboard_title),
            onPowerClick = onLogout,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppBorder),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonOutline,
                    contentDescription = null,
                    tint = BankPurple,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.dashboard_hello, account.holder),
                    color = AppText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.dashboard_last_login, "07/06/2026 09:37"),
                    color = AppMuted,
                    fontSize = 11.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Tile(
                    icon = Icons.Filled.Folder,
                    label = stringResource(R.string.dashboard_tile_accounts),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                Tile(
                    icon = Icons.Filled.SwapHoriz,
                    label = stringResource(R.string.dashboard_tile_transfers),
                    onClick = onTransferClicked,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Tile(
                    icon = Icons.Filled.ViewList,
                    label = stringResource(R.string.dashboard_tile_services),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
                Tile(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = stringResource(R.string.dashboard_tile_contact),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(BankPurple),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "  Solde disponible : ${formatMga(account.balanceMga)} MGA",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun Tile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, AppBorder),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFAFAFB), Color(0xFFE8E6EE)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = BankPurple,
                    modifier = Modifier.size(46.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = label,
                    color = BankPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun formatMga(amount: Long): String {
    val nf = NumberFormat.getInstance(Locale.FRANCE)
    return nf.format(amount)
}

internal fun formatMgaPublic(amount: Long): String = formatMga(amount)
