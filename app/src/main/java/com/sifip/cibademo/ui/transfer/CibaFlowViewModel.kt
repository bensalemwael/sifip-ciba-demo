package com.sifip.cibademo.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sifip.cibademo.data.mock.CibaService
import com.sifip.cibademo.data.mock.MockScenario
import com.sifip.cibademo.data.model.CibaScope
import com.sifip.cibademo.data.model.ConsentDecision
import com.sifip.cibademo.data.model.ConsentRequest
import com.sifip.cibademo.data.model.FraudResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransferFormState(
    val recipient: String = "Mialy Rakotomalala",
    val iban: String = "MG46 0000 5012 3456 7890 12",
    val amountText: String = "250000",
    val motif: String = "Loyer juin",
)

data class CibaFlowUiState(
    val form: TransferFormState = TransferFormState(),
    val scenario: MockScenario = MockScenario.APPROVE_LOW_FRAUD,
    val pendingRequest: ConsentRequest? = null,
    val decision: ConsentDecision? = null,
    val fraudResult: FraudResult? = null,
    val activationRunning: Boolean = false,
)

/**
 * ViewModel partagé sur tout le flux CIBA — Transfer → Waiting → Consent
 * → Activation → Success. Scope au niveau Activity (via la factory), donc
 * la même instance est partagée entre toutes les destinations Compose
 * Navigation.
 */
class CibaFlowViewModel(
    private val cibaService: CibaService,
    private val bankName: String,
    private val bankLogoResId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(
        CibaFlowUiState(scenario = cibaService.scenario.value),
    )
    val state: StateFlow<CibaFlowUiState> = _state.asStateFlow()

    val smsReceived: StateFlow<Boolean> = cibaService.smsReceived

    // Form
    fun onRecipientChanged(v: String) = _state.update { it.copy(form = it.form.copy(recipient = v)) }
    fun onIbanChanged(v: String) = _state.update { it.copy(form = it.form.copy(iban = v)) }
    fun onAmountChanged(v: String) = _state.update {
        it.copy(form = it.form.copy(amountText = v.filter(Char::isDigit)))
    }
    fun onMotifChanged(v: String) = _state.update { it.copy(form = it.form.copy(motif = v)) }

    fun setScenario(scenario: MockScenario) {
        cibaService.setScenario(scenario)
        _state.update { it.copy(scenario = scenario) }
    }

    /**
     * Stage 2 — la banque envoie la requête CIBA à SIFIP. Le mock simule
     * une latence + l'arrivée du SMS sur le device.
     */
    fun submitTransfer() {
        viewModelScope.launch {
            val amount = _state.value.form.amountText.toLongOrNull() ?: 0L
            val req = cibaService.requestConsent(
                bankName = bankName,
                bankLogoResId = bankLogoResId,
                scopes = listOf(
                    CibaScope.READ_ACCOUNT,
                    CibaScope.NUMBER_VERIFY,
                    CibaScope.SIM_SWAP,
                    CibaScope.FRAUD_ENGINE,
                ),
                amountMga = amount,
                recipient = _state.value.form.recipient,
            )
            _state.update { it.copy(pendingRequest = req) }
        }
    }

    /** L'utilisateur a tapé la bannière SMS — équivalent du clic deeplink. */
    fun consumeSms() {
        cibaService.markSmsConsumed()
    }

    /** Stage 3 — décision sur l'écran de consentement SIFIP. */
    fun submitDecision(decision: ConsentDecision) {
        cibaService.submitDecision(decision)
        _state.update { it.copy(decision = decision) }
        if (decision == ConsentDecision.Rejected) {
            // La requête est purgée par le service
            _state.update { it.copy(pendingRequest = null) }
        }
    }

    /** Stage 4 — activation Fraud Engine + scoring. */
    fun runActivation() {
        if (_state.value.activationRunning) return
        viewModelScope.launch {
            _state.update { it.copy(activationRunning = true) }
            val amount = _state.value.form.amountText.toLongOrNull() ?: 0L
            val result = cibaService.activateAndScore(amountMga = amount)
            _state.update { it.copy(fraudResult = result, activationRunning = false) }
        }
    }

    /** Stage 5 — reset après retour au dashboard. */
    fun reset() {
        _state.update {
            CibaFlowUiState(
                form = it.form, // garde la forme remplie pour démos répétées
                scenario = it.scenario,
            )
        }
    }
}
