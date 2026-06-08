package com.sifip.cibademo.data.mock

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Réponses des 3 APIs SIFIP utilisées au login, schémas proches des
 * réponses réelles documentées dans sifip-platform/docs/COLLEAGUE-GUIDE.md.
 */
data class NumberVerifyResponse(val verified: Boolean, val message: String)
data class SimSwapResponse(val swapped: Boolean, val message: String)
data class DeviceSwapResponse(val knownDevice: Boolean, val message: String)

/**
 * Contrat des APIs SIFIP utilisées au login. Swap vers une vraie
 * implémentation Retrofit en 1 ligne dans CibaApplication.
 */
interface SifipAuthApi {
    suspend fun verifyNumber(msisdn: String): NumberVerifyResponse
    suspend fun checkSimSwap(msisdn: String): SimSwapResponse
    suspend fun checkDeviceSwap(msisdn: String, deviceId: String): DeviceSwapResponse
}

/**
 * Mock in-memory : latences réalistes (450–650 ms) pour que les checks
 * animés à l'écran soient visibles à l'œil nu.
 */
class SifipAuthMock(initial: AuthScenario = AuthScenario.ALL_OK) : SifipAuthApi {

    private val _scenario = MutableStateFlow(initial)
    val scenario: StateFlow<AuthScenario> = _scenario.asStateFlow()

    fun setScenario(next: AuthScenario) {
        _scenario.value = next
    }

    override suspend fun verifyNumber(msisdn: String): NumberVerifyResponse {
        delay(650)
        val ok = _scenario.value != AuthScenario.FAIL_NUMBER_VERIFY
        return NumberVerifyResponse(
            verified = ok,
            message = if (ok) {
                "Numéro vérifié auprès de l'opérateur"
            } else {
                "Le numéro fourni ne correspond pas au compte"
            },
        )
    }

    override suspend fun checkSimSwap(msisdn: String): SimSwapResponse {
        delay(550)
        val swapped = _scenario.value == AuthScenario.FAIL_SIM_SWAP
        return SimSwapResponse(
            swapped = swapped,
            message = if (swapped) {
                "SIM changée il y a moins de 72 h — accès bloqué"
            } else {
                "Aucun changement de SIM récent"
            },
        )
    }

    override suspend fun checkDeviceSwap(
        msisdn: String,
        deviceId: String,
    ): DeviceSwapResponse {
        delay(450)
        val known = _scenario.value != AuthScenario.FAIL_DEVICE_SWAP
        return DeviceSwapResponse(
            knownDevice = known,
            message = if (known) {
                "Appareil reconnu"
            } else {
                "Appareil inconnu — second facteur requis"
            },
        )
    }
}
