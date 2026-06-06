package com.sifip.cibademo.ui.transfer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.R
import com.sifip.cibademo.data.mock.MockScenario
import com.sifip.cibademo.ui.components.AppPrimaryButton
import com.sifip.cibademo.ui.components.BankTopBar
import com.sifip.cibademo.ui.theme.AppBorder
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.AppText
import com.sifip.cibademo.ui.theme.BankPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: CibaFlowViewModel,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val form = state.form

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        BankTopBar(
            title = stringResource(R.string.transfer_title),
            onHomeClick = onBack,
            onPowerClick = onBack,
        )

        // Sélecteur scénario CIBA — caché en haut à droite via dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Scénario démo : ${state.scenario.label}",
                color = AppMuted,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f),
            )
            ScenarioPicker(current = state.scenario, onSelected = viewModel::setScenario)
        }

        AccountSelector(
            label = stringResource(R.string.transfer_debit_label),
            value = "Compte courant •••• 4218",
        )
        Spacer(modifier = Modifier.height(8.dp))
        AccountSelector(
            label = stringResource(R.string.transfer_credit_label),
            value = form.recipient.ifBlank { "—" },
        )
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(6.dp),
            color = Color.White,
            border = BorderStroke(1.dp, AppBorder),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FieldGroup(label = stringResource(R.string.transfer_recipient)) {
                    OutlinedTextField(
                        value = form.recipient,
                        onValueChange = viewModel::onRecipientChanged,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BankPurple),
                    )
                }
                FieldGroup(label = stringResource(R.string.transfer_iban)) {
                    OutlinedTextField(
                        value = form.iban,
                        onValueChange = viewModel::onIbanChanged,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BankPurple),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FieldGroup(
                        label = stringResource(R.string.transfer_amount),
                        modifier = Modifier.weight(1f),
                    ) {
                        OutlinedTextField(
                            value = form.amountText,
                            onValueChange = viewModel::onAmountChanged,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BankPurple),
                        )
                    }
                    FieldGroup(label = "Devise", modifier = Modifier.size(width = 96.dp, height = 70.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.dp, AppBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = stringResource(R.string.transfer_currency),
                                color = AppText,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
                FieldGroup(label = stringResource(R.string.transfer_motif)) {
                    OutlinedTextField(
                        value = form.motif,
                        onValueChange = viewModel::onMotifChanged,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BankPurple),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 80.dp),
        ) {
            AppPrimaryButton(
                text = stringResource(R.string.transfer_button),
                onClick = {
                    viewModel.submitTransfer()
                    onSubmit()
                },
                enabled = form.amountText.isNotBlank() && form.iban.isNotBlank(),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AccountSelector(label: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, AppBorder),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFAFAFB), Color(0xFFE8E6EE)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = label, color = AppText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(text = value, color = AppMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun FieldGroup(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = AppMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun ScenarioPicker(
    current: MockScenario,
    onSelected: (MockScenario) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = "Changer de scénario",
                tint = AppMuted,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MockScenario.values().forEach { scenario ->
                DropdownMenuItem(
                    text = { Text(scenario.label) },
                    onClick = {
                        onSelected(scenario)
                        expanded = false
                    },
                )
            }
        }
    }
}
