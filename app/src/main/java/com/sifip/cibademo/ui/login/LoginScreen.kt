package com.sifip.cibademo.ui.login

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sifip.cibademo.R
import com.sifip.cibademo.data.mock.AuthScenario
import com.sifip.cibademo.ui.components.AppPrimaryButton
import com.sifip.cibademo.ui.components.AppSecondaryButton
import com.sifip.cibademo.ui.components.CheckStepView
import com.sifip.cibademo.ui.theme.AppBorder
import com.sifip.cibademo.ui.theme.AppMuted
import com.sifip.cibademo.ui.theme.BankPurple
import com.sifip.cibademo.ui.theme.BankPurpleDeep
import com.sifip.cibademo.ui.theme.StatusError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onAuthenticated: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        // Top bar violet avec scénario picker (caché à droite)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BankPurple)
                .height(48.dp),
        ) {
            Text(
                text = stringResource(R.string.login_title),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 56.dp),
            )
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                AuthScenarioPicker(
                    current = state.scenario,
                    onSelected = viewModel::setScenario,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            color = BankPurpleDeep,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Carte formulaire : login + password + checks SIFIP
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(1.dp, AppBorder),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FieldLabel("Login")
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = viewModel::onPhoneChanged,
                    placeholder = { Text(stringResource(R.string.login_phone_hint)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.PersonOutline,
                            contentDescription = null,
                            tint = BankPurple,
                        )
                    },
                    enabled = state.phase != LoginPhase.Running,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BankPurple),
                )

                FieldLabel("Mot de passe")
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = { Text("••••••") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = BankPurple,
                        )
                    },
                    enabled = state.phase != LoginPhase.Running,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BankPurple),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "CONTRÔLES DE SÉCURITÉ SIFIP",
                    color = AppMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                )
                HorizontalDivider(color = AppBorder, thickness = 1.dp)

                CheckStepView(row = state.numberVerify, indicatorColor = BankPurple)
                CheckStepView(row = state.simSwap, indicatorColor = BankPurple)
                CheckStepView(row = state.deviceSwap, indicatorColor = BankPurple)
                CheckStepView(row = state.authorization, indicatorColor = BankPurple)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bouton Valider / Réessayer centré
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp),
        ) {
            when (state.phase) {
                LoginPhase.Failure -> {
                    Text(
                        text = "Connexion bloquée par le contrôle SIFIP.",
                        color = StatusError,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                    )
                    AppSecondaryButton(
                        text = "Réessayer",
                        onClick = viewModel::reset,
                    )
                }
                else -> {
                    AppPrimaryButton(
                        text = stringResource(R.string.login_button),
                        onClick = { viewModel.login(onAuthenticated) },
                        loading = state.phase == LoginPhase.Running,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = AppMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun AuthScenarioPicker(
    current: AuthScenario,
    onSelected: (AuthScenario) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "Scénario : ${current.label}",
                    tint = Color.White,
                )
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AuthScenario.values().forEach { scenario ->
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
