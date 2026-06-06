package com.sifip.cibademo.ui.success

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.R
import com.sifip.cibademo.data.model.ConsentDecision
import com.sifip.cibademo.data.model.FraudDecision
import com.sifip.cibademo.ui.components.AppPrimaryButton
import com.sifip.cibademo.ui.components.BankTopBar
import com.sifip.cibademo.ui.theme.AppBorder
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.AppText
import com.sifip.cibademo.ui.theme.BankPurpleDeep
import com.sifip.cibademo.ui.theme.StatusError
import com.sifip.cibademo.ui.theme.StatusOk
import com.sifip.cibademo.ui.transfer.CibaFlowViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SuccessScreen(
    viewModel: CibaFlowViewModel,
    onBackToDashboard: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val outcome: SuccessOutcome = when {
        state.decision == ConsentDecision.Rejected -> SuccessOutcome.RejectedByUser
        state.fraudResult?.decision in setOf(FraudDecision.REJECT, FraudDecision.CHALLENGE) ->
            SuccessOutcome.RejectedByFraud
        state.fraudResult?.decision == FraudDecision.APPROVE -> SuccessOutcome.Approved
        else -> SuccessOutcome.Approved // fallback
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        BankTopBar(
            title = stringResource(R.string.transfer_title),
            onHomeClick = onBackToDashboard,
            onPowerClick = onBackToDashboard,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Bandeau résultat
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = if (outcome == SuccessOutcome.Approved) StatusOk else StatusError,
                        shape = RoundedCornerShape(36.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (outcome == SuccessOutcome.Approved)
                        Icons.Filled.CheckCircle
                    else
                        Icons.Filled.Block,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = when (outcome) {
                    SuccessOutcome.Approved -> stringResource(R.string.success_title)
                    SuccessOutcome.RejectedByFraud -> stringResource(R.string.rejected_by_fraud_title)
                    SuccessOutcome.RejectedByUser -> stringResource(R.string.rejected_by_user_title)
                },
                color = if (outcome == SuccessOutcome.Approved) StatusOk else StatusError,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            if (outcome == SuccessOutcome.RejectedByUser) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.rejected_by_user_subtitle),
                    color = AppMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Carte détails du virement
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, AppBorder),
                shadowElevation = 1.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("DÉTAIL DU VIREMENT")
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "Bénéficiaire", value = state.form.recipient)
                    DetailRow(label = "IBAN", value = state.form.iban)
                    DetailRow(label = "Motif", value = state.form.motif)
                    DetailRow(
                        label = "Montant",
                        value = "${formatMga(state.form.amountText.toLongOrNull() ?: 0L)} MGA",
                    )

                    if (state.fraudResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = AppBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionTitle("SCORE DE FRAUDE SIFIP")
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailRow(
                            label = "Score IA",
                            value = "${state.fraudResult!!.score} %",
                            valueColor = if (outcome == SuccessOutcome.Approved) StatusOk else StatusError,
                        )
                        state.fraudResult!!.reasons.take(4).forEach { reason ->
                            Text(
                                text = "• $reason",
                                color = AppText.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                AppPrimaryButton(
                    text = stringResource(R.string.success_back),
                    onClick = {
                        viewModel.reset()
                        onBackToDashboard()
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AppMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.2.sp,
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = BankPurpleDeep,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = AppMuted, fontSize = 12.sp)
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun formatMga(amount: Long): String {
    val nf = NumberFormat.getInstance(Locale.FRANCE)
    return nf.format(amount)
}

private enum class SuccessOutcome { Approved, RejectedByFraud, RejectedByUser }
