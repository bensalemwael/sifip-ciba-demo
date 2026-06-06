package com.sifip.cibademo.data.model

/**
 * Périmètre d'autorisation OAuth2 demandé par la banque à SIFIP.
 * Modélise un sous-ensemble des scopes documentés dans
 * sifip-platform/docs/COLLEAGUE-GUIDE.md.
 */
enum class CibaScope(
    val code: String,
    val label: String,
    val description: String,
    val durationLabel: String,
) {
    READ_ACCOUNT(
        code = "sifip:read-account",
        label = "Lecture du compte",
        description = "Solde et bénéficiaires habituels",
        durationLabel = "24h",
    ),
    NUMBER_VERIFY(
        code = "sifip:number-verify",
        label = "Vérification réseau mobile",
        description = "Confirmation du numéro auprès de l'opérateur",
        durationLabel = "Ponctuel",
    ),
    SIM_SWAP(
        code = "sifip:sim-swap",
        label = "Vérification ligne mobile",
        description = "Détection d'un changement SIM récent",
        durationLabel = "Ponctuel",
    ),
    FRAUD_ENGINE(
        code = "sifip:fraud-engine",
        label = "Analyse anti-fraude IA",
        description = "Scoring de la transaction par le modèle SIFIP",
        durationLabel = "Ponctuel",
    ),
}

/**
 * Demande de consentement CIBA envoyée par la banque à SIFIP. Sert de
 * payload au "deeplink" SMS et alimente l'écran de consentement.
 */
data class ConsentRequest(
    val requestId: String,
    val bankName: String,
    val bankLogoResId: Int,
    val scopes: List<CibaScope>,
    val amountMga: Long,
    val recipient: String,
    val validityHours: Int = 24,
)

/** Décision de l'utilisateur sur la demande de consentement. */
enum class ConsentDecision { Approved, Rejected }
