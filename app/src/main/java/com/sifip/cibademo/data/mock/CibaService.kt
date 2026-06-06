package com.sifip.cibademo.data.mock

import com.sifip.cibademo.data.model.CibaScope
import com.sifip.cibademo.data.model.ConsentDecision
import com.sifip.cibademo.data.model.ConsentRequest
import com.sifip.cibademo.data.model.FraudDecision
import com.sifip.cibademo.data.model.FraudResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Service mock qui orchestre tout le flux CIBA pour la démo :
 *   1) bank.requestConsent(...) → renvoie une requête + déclenche l'arrivée SMS
 *   2) écran de consentement → user.submitDecision(...)
 *   3) si Approved → activateAndScore(...) → renvoie le score de fraude
 *
 * État partagé via [pendingRequest] (StateFlow) : le NavGraph observe ce
 * StateFlow pour afficher la bannière SMS dès qu'une requête est créée.
 */
class CibaService(initialScenario: MockScenario = MockScenario.APPROVE_LOW_FRAUD) {

    private val _scenario = MutableStateFlow(initialScenario)
    val scenario: StateFlow<MockScenario> = _scenario.asStateFlow()

    private val _pendingRequest = MutableStateFlow<ConsentRequest?>(null)
    val pendingRequest: StateFlow<ConsentRequest?> = _pendingRequest.asStateFlow()

    private val _smsReceived = MutableStateFlow(false)
    val smsReceived: StateFlow<Boolean> = _smsReceived.asStateFlow()

    fun setScenario(next: MockScenario) {
        _scenario.value = next
    }

    /**
     * Étape 1 — La banque (BMOI) initie une demande de consentement
     * auprès de SIFIP. Le mock simule l'envoi du SMS après ~1.5 s.
     */
    suspend fun requestConsent(
        bankName: String,
        bankLogoResId: Int,
        scopes: List<CibaScope>,
        amountMga: Long,
        recipient: String,
    ): ConsentRequest {
        delay(BANK_TO_SIFIP_LATENCY_MS)
        val req = ConsentRequest(
            requestId = generateRequestId(),
            bankName = bankName,
            bankLogoResId = bankLogoResId,
            scopes = scopes,
            amountMga = amountMga,
            recipient = recipient,
        )
        _pendingRequest.value = req
        delay(SMS_DELIVERY_LATENCY_MS)
        _smsReceived.value = true
        return req
    }

    /**
     * Étape 2 — Quand la bannière SMS est tapée et que l'écran de
     * consentement s'ouvre, le NavGraph appelle ceci pour stopper l'auto
     * "ré-affichage" de la bannière.
     */
    fun markSmsConsumed() {
        _smsReceived.value = false
    }

    /**
     * Étape 3 — L'utilisateur soumet sa décision sur l'écran de
     * consentement SIFIP.
     */
    fun submitDecision(decision: ConsentDecision) {
        if (decision == ConsentDecision.Rejected) {
            // Cleanup la requête pour ne pas la rejouer
            _pendingRequest.value = null
        }
    }

    /**
     * Étape 4 — Après consentement, SIFIP active le Fraud Engine et
     * score la transaction.
     */
    suspend fun activateAndScore(amountMga: Long): FraudResult {
        delay(SIFIP_CONNECT_LATENCY_MS)
        delay(FRAUD_ACTIVATE_LATENCY_MS)
        delay(FRAUD_ANALYZE_LATENCY_MS)

        return when (_scenario.value) {
            MockScenario.APPROVE_HIGH_FRAUD -> FraudResult(
                score = 87,
                decision = FraudDecision.REJECT,
                reasons = listOf(
                    "Authentification comportementale : incohérente",
                    "Vérification historique : opération atypique",
                    "Bénéficiaire jamais utilisé",
                    "Montant > 10× la moyenne mensuelle",
                ),
            )
            MockScenario.AMOUNT_BASED -> if (amountMga > AMOUNT_FRAUD_THRESHOLD_MGA) {
                FraudResult(
                    score = 76,
                    decision = FraudDecision.REJECT,
                    reasons = listOf(
                        "Authentification comportementale : OK",
                        "Vérification historique : montant inhabituel",
                        "Bénéficiaire connu",
                        "Montant > seuil critique (1 000 000 MGA)",
                    ),
                )
            } else {
                lowScoreOk()
            }
            else -> lowScoreOk()
        }.also {
            _pendingRequest.value = null
        }
    }

    private fun lowScoreOk() = FraudResult(
        score = 12,
        decision = FraudDecision.APPROVE,
        reasons = listOf(
            "Authentification comportementale : OK",
            "Vérification historique : conforme",
            "Bénéficiaire connu",
            "Montant cohérent avec l'historique",
        ),
    )

    private var requestCounter = 100L
    private fun generateRequestId(): String = "ciba-${(requestCounter++).toString(16)}"

    private companion object {
        const val BANK_TO_SIFIP_LATENCY_MS = 400L
        const val SMS_DELIVERY_LATENCY_MS = 1_400L
        const val SIFIP_CONNECT_LATENCY_MS = 600L
        const val FRAUD_ACTIVATE_LATENCY_MS = 700L
        const val FRAUD_ANALYZE_LATENCY_MS = 900L
        const val AMOUNT_FRAUD_THRESHOLD_MGA = 1_000_000L
    }
}
