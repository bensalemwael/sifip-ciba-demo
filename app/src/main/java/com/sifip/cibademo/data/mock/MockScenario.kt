package com.sifip.cibademo.data.mock

/**
 * Scénario qui pilote le mock CIBA + Fraud Engine pour la démo.
 *
 * Commutable au runtime (sélecteur sur l'écran Transfer) ou au build
 * via `./gradlew assembleDebug -PcibaScenario=USER_REJECTS`.
 */
enum class MockScenario(val label: String) {
    APPROVE_LOW_FRAUD("Consentement approuvé · score 12 % → virement OK"),
    APPROVE_HIGH_FRAUD("Consentement approuvé · score 87 % → virement bloqué"),
    USER_REJECTS("Consentement refusé par l'utilisateur"),
    AMOUNT_BASED("Score selon le montant (seuil 1 000 000 MGA)"),
}
