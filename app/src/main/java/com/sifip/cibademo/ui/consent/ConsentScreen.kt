package com.sifip.cibademo.ui.consent

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.R
import com.sifip.cibademo.data.model.CibaScope
import com.sifip.cibademo.data.model.ConsentDecision
import com.sifip.cibademo.data.model.ConsentRequest
import com.sifip.cibademo.ui.components.AppPrimaryButton
import com.sifip.cibademo.ui.components.AppSecondaryButton
import com.sifip.cibademo.ui.components.SifipLogo
import com.sifip.cibademo.ui.components.SifipTopBar
import com.sifip.cibademo.ui.theme.AppBorder
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.AppText
import com.sifip.cibademo.ui.theme.SifipBlue
import com.sifip.cibademo.ui.theme.SifipBlueDeep
import com.sifip.cibademo.ui.theme.SifipBlueLight
import com.sifip.cibademo.ui.theme.SifipGold
import com.sifip.cibademo.ui.theme.StatusError
import com.sifip.cibademo.ui.transfer.CibaFlowViewModel

@Composable
fun ConsentScreen(
    viewModel: CibaFlowViewModel,
    onApproved: () -> Unit,
    onRejected: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val request = state.pendingRequest

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
    ) {
        SifipTopBar(
            title = stringResource(R.string.consent_title),
            onBackClick = onBack,
        )

        if (request == null) {
            // Pas de requête en attente — affichage simple
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Aucune demande de consentement en attente.",
                    color = AppMuted,
                    textAlign = TextAlign.Center,
                )
            }
            return@Column
        }

        // Header SIFIP
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SifipBlueLight)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SifipLogo(width = 72.dp, height = 72.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SIFIP",
                color = SifipBlueDeep,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
            )
            Text(
                text = "Serveur d'autorisation",
                color = SifipBlue,
                fontSize = 12.sp,
            )
        }

        // Carte demande
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, AppBorder),
            shadowElevation = 1.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, AppBorder),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = request.bankLogoResId),
                                contentDescription = request.bankName,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = request.bankName,
                            color = AppText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = stringResource(R.string.consent_bank_intro),
                            color = AppMuted,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }

        // Liste des scopes
        Text(
            text = stringResource(R.string.consent_scopes_header).uppercase(),
            color = AppMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            request.scopes.forEach { scope ->
                ScopeRow(scope)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Validité
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = null,
                tint = SifipGold,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.consent_validity),
                color = AppText,
                fontSize = 13.sp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Boutons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppSecondaryButton(
                text = stringResource(R.string.consent_reject),
                onClick = {
                    viewModel.submitDecision(ConsentDecision.Rejected)
                    onRejected()
                },
                borderColor = StatusError,
                contentColor = StatusError,
                modifier = Modifier.weight(1f),
            )
            AppPrimaryButton(
                text = stringResource(R.string.consent_approve),
                onClick = {
                    viewModel.submitDecision(ConsentDecision.Approved)
                    onApproved()
                },
                containerColor = SifipBlue,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SifipBlueLight)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.consent_footer),
                color = SifipBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ScopeRow(scope: CibaScope) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = SifipBlueLight,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SifipBlue),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scope.label,
                    color = SifipBlueDeep,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = scope.description,
                    color = AppText.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                )
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SifipGold.copy(alpha = 0.15f),
            ) {
                Text(
                    text = scope.durationLabel,
                    color = SifipBlueDeep,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

// Helper exposed pour passer ConsentRequest dans des previews/tests
@Suppress("unused")
internal fun ConsentRequest.summary(): String =
    "${scopes.size} scopes vers $bankName (validité ${validityHours}h)"
