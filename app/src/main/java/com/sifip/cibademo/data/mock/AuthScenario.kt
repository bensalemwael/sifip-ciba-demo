package com.sifip.cibademo.data.mock

/**
 * Scénario qui pilote les 3 APIs SIFIP utilisées au login (data plane).
 *
 * Distinct de [MockScenario] qui pilote le flux CIBA + Fraud Engine au
 * moment du virement (control plane). Les deux scénarios sont
 * composables : ex. AuthScenario.ALL_OK + MockScenario.AMOUNT_BASED →
 * login passe, fraude évaluée selon montant.
 */
enum class AuthScenario(val label: String) {
    ALL_OK("Tous les contrôles SIFIP OK"),
    FAIL_NUMBER_VERIFY("Échec Vérification réseau mobile"),
    FAIL_SIM_SWAP("Échec Vérification ligne mobile (SIM changée)"),
    FAIL_DEVICE_SWAP("Échec Vérification Smartphone (appareil inconnu)"),
}
