package com.sifip.cibademo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sifip.cibademo.data.mock.AuthScenario
import com.sifip.cibademo.data.mock.SifipAuthMock
import com.sifip.cibademo.ui.components.CheckRow
import com.sifip.cibademo.ui.components.CheckStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LoginPhase { Idle, Running, Success, Failure }

data class LoginUiState(
    val phoneNumber: String = "+261 32 12 345 67",
    val password: String = "",
    val phase: LoginPhase = LoginPhase.Idle,
    val numberVerify: CheckRow = CheckRow("Vérification réseau mobile", CheckStatus.Idle),
    val simSwap: CheckRow = CheckRow("Vérification ligne mobile", CheckStatus.Idle),
    val deviceSwap: CheckRow = CheckRow("Vérification Smartphone", CheckStatus.Idle),
    val authorization: CheckRow = CheckRow("Autorisation SIFIP", CheckStatus.Idle),
    val scenario: AuthScenario = AuthScenario.ALL_OK,
)

/**
 * Orchestre les 3 APIs SIFIP en séquence (Number Verify → SIM Swap →
 * Device Swap) puis l'autorisation finale. Si une étape échoue, le flux
 * s'arrête et `phase` passe à Failure. Le bouton "Réessayer" remet à zéro.
 */
class LoginViewModel(private val sifipAuth: SifipAuthMock) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginUiState(scenario = sifipAuth.scenario.value),
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onPhoneChanged(v: String) = _state.update { it.copy(phoneNumber = v) }
    fun onPasswordChanged(v: String) = _state.update { it.copy(password = v) }

    fun setScenario(scenario: AuthScenario) {
        sifipAuth.setScenario(scenario)
        _state.update { it.copy(scenario = scenario) }
    }

    fun reset() {
        _state.update {
            LoginUiState(
                phoneNumber = it.phoneNumber,
                password = it.password,
                scenario = it.scenario,
            )
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (_state.value.phase == LoginPhase.Running) return

        viewModelScope.launch {
            // Reset checks, marque Number Verify Running
            _state.update {
                it.copy(
                    phase = LoginPhase.Running,
                    numberVerify = it.numberVerify.copy(
                        status = CheckStatus.Running,
                        message = null,
                    ),
                    simSwap = it.simSwap.copy(status = CheckStatus.Idle, message = null),
                    deviceSwap = it.deviceSwap.copy(status = CheckStatus.Idle, message = null),
                    authorization = it.authorization.copy(status = CheckStatus.Idle, message = null),
                )
            }

            val phone = _state.value.phoneNumber

            // 1) Number Verify
            val nv = sifipAuth.verifyNumber(phone)
            _state.update {
                it.copy(
                    numberVerify = it.numberVerify.copy(
                        status = if (nv.verified) CheckStatus.Ok else CheckStatus.Failed,
                        message = nv.message,
                    ),
                    simSwap = if (nv.verified) {
                        it.simSwap.copy(status = CheckStatus.Running)
                    } else {
                        it.simSwap
                    },
                )
            }
            if (!nv.verified) {
                _state.update { it.copy(phase = LoginPhase.Failure) }
                return@launch
            }

            // 2) SIM Swap
            val ss = sifipAuth.checkSimSwap(phone)
            _state.update {
                it.copy(
                    simSwap = it.simSwap.copy(
                        status = if (!ss.swapped) CheckStatus.Ok else CheckStatus.Failed,
                        message = ss.message,
                    ),
                    deviceSwap = if (!ss.swapped) {
                        it.deviceSwap.copy(status = CheckStatus.Running)
                    } else {
                        it.deviceSwap
                    },
                )
            }
            if (ss.swapped) {
                _state.update { it.copy(phase = LoginPhase.Failure) }
                return@launch
            }

            // 3) Device Swap
            val ds = sifipAuth.checkDeviceSwap(phone, deviceId = DEMO_DEVICE_ID)
            _state.update {
                it.copy(
                    deviceSwap = it.deviceSwap.copy(
                        status = if (ds.knownDevice) CheckStatus.Ok else CheckStatus.Failed,
                        message = ds.message,
                    ),
                )
            }
            if (!ds.knownDevice) {
                _state.update { it.copy(phase = LoginPhase.Failure) }
                return@launch
            }

            // 4) Autorisation finale SIFIP — accordée si les 3 contrôles passent
            _state.update {
                it.copy(authorization = it.authorization.copy(status = CheckStatus.Running))
            }
            delay(AUTHORIZATION_LATENCY_MS)
            _state.update {
                it.copy(
                    authorization = it.authorization.copy(
                        status = CheckStatus.Ok,
                        message = "Autorisation valide — accès sécurisé accordé",
                    ),
                    phase = LoginPhase.Success,
                )
            }
            onSuccess()
        }
    }

    private companion object {
        // Fingerprint stable du device pour la démo. En prod, viendrait
        // de SafetyNet / Play Integrity.
        const val DEMO_DEVICE_ID = "samsung-galaxy-DEMO-CIBA-A1B2C3"
        const val AUTHORIZATION_LATENCY_MS = 350L
    }
}
