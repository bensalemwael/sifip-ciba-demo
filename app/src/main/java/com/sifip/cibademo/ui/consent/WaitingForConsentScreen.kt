package com.sifip.cibademo.ui.consent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.R
import com.sifip.cibademo.ui.components.BankTopBar
import com.sifip.cibademo.ui.components.SmsBanner
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.BankPurple
import com.sifip.cibademo.ui.theme.BankPurpleDeep
import com.sifip.cibademo.ui.transfer.CibaFlowViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WaitingForConsentScreen(
    viewModel: CibaFlowViewModel,
    onSmsTapped: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val smsVisible by viewModel.smsReceived.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BankTopBar(
                title = stringResource(R.string.transfer_title),
                onHomeClick = onBack,
                onPowerClick = onBack,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    color = BankPurple,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.waiting_title),
                    color = BankPurpleDeep,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.waiting_subtitle),
                    color = AppMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Bannière SMS en haut (slide-down quand reçue)
        val amount = state.form.amountText.toLongOrNull() ?: 0L
        val preview = stringResource(
            R.string.waiting_sms_preview,
            formatMga(amount),
            state.form.recipient,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp), // sous la BankTopBar
        ) {
            SmsBanner(
                visible = smsVisible,
                sender = stringResource(R.string.waiting_sms_sender),
                preview = preview,
                onTap = {
                    viewModel.consumeSms()
                    onSmsTapped()
                },
            )
        }
    }
}

private fun formatMga(amount: Long): String {
    val nf = NumberFormat.getInstance(Locale.FRANCE)
    return nf.format(amount)
}
