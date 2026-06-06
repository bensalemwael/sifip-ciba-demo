package com.sifip.cibademo.ui.activation

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.R
import com.sifip.cibademo.ui.components.BankTopBar
import com.sifip.cibademo.ui.components.CheckRow
import com.sifip.cibademo.ui.components.CheckStatus
import com.sifip.cibademo.ui.components.CheckStepView
import com.sifip.cibademo.ui.components.SifipLogo
import com.sifip.cibademo.ui.theme.AppBorder
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.SifipBlue
import com.sifip.cibademo.ui.theme.SifipBlueDeep
import com.sifip.cibademo.ui.transfer.CibaFlowViewModel
import kotlinx.coroutines.delay

@Composable
fun ActivationScreen(
    viewModel: CibaFlowViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var step1 by remember { mutableStateOf(CheckStatus.Running) }
    var step2 by remember { mutableStateOf(CheckStatus.Idle) }
    var step3 by remember { mutableStateOf(CheckStatus.Idle) }

    LaunchedEffect(Unit) {
        viewModel.runActivation()
    }

    // Animation locale des 3 steps (synchronisée avec les latences du service)
    LaunchedEffect(Unit) {
        delay(600)
        step1 = CheckStatus.Ok
        step2 = CheckStatus.Running
        delay(700)
        step2 = CheckStatus.Ok
        step3 = CheckStatus.Running
        delay(900)
        step3 = CheckStatus.Ok
        delay(300)
        onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        BankTopBar(
            title = stringResource(R.string.activation_title),
            onHomeClick = onBack,
            onPowerClick = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SifipLogo(width = 36.dp, height = 36.dp)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "via SIFIP",
                    color = SifipBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.activation_title),
                color = SifipBlueDeep,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = androidx.compose.ui.graphics.Color.White,
                border = BorderStroke(1.dp, AppBorder),
                shadowElevation = 1.dp,
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    CheckStepView(
                        row = CheckRow(
                            label = stringResource(R.string.activation_step_connect),
                            status = step1,
                            message = if (step1 == CheckStatus.Ok) "Token CIBA délivré" else null,
                        ),
                        indicatorColor = SifipBlue,
                    )
                    CheckStepView(
                        row = CheckRow(
                            label = stringResource(R.string.activation_step_activate),
                            status = step2,
                            message = if (step2 == CheckStatus.Ok) "Modèle IA disponible" else null,
                        ),
                        indicatorColor = SifipBlue,
                    )
                    CheckStepView(
                        row = CheckRow(
                            label = stringResource(R.string.activation_step_analyze),
                            status = step3,
                            message = state.fraudResult?.let { "Score : ${it.score} %" },
                        ),
                        indicatorColor = SifipBlue,
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Veuillez patienter…",
                color = AppMuted,
                fontSize = 13.sp,
            )
        }

        // Footer-fix : laisse remplir l'espace
        Box(modifier = Modifier.fillMaxWidth().height(1.dp))
    }
}
