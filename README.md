# SIFIP CIBA — Demo Android

Application Android native (Kotlin + Jetpack Compose + Material 3) qui
illustre le flux **CIBA** (Client-Initiated Backchannel Authentication,
OAuth 2.0) appliqué à l'activation du service **Fraud Engine SIFIP** par
une banque (BMOI Madagascar dans l'exemple).

L'app simule sur un seul device les **3 acteurs** d'un flux CIBA réel :
1. **Application bancaire** (BMOI) : initie le virement → demande
   l'autorisation à SIFIP.
2. **Serveur d'autorisation SIFIP** : envoie un SMS deeplink à l'utilisateur
   puis sert l'écran de consentement.
3. **Utilisateur** : reçoit le SMS, ouvre le deeplink, approuve / refuse.

---

## 1. Scénario complet pas-à-pas

| Étape | Acteur | Action | Écran |
| :---- | :----- | :----- | :---- |
| 1 | Utilisateur | Ouvre l'app, s'identifie | `LoginScreen` |
| 2 | Utilisateur | Va sur l'accueil, touche la tuile **Virements** | `DashboardScreen` |
| 3 | Utilisateur | Remplit le formulaire (bénéficiaire, IBAN, montant, motif) et touche **Valider** | `TransferScreen` |
| 4 | BMOI | Envoie une requête CIBA à SIFIP avec les scopes demandés | `CibaService.requestConsent()` |
| 5 | SIFIP | Délivre un SMS deeplink au numéro de l'utilisateur (~1.5 s de latence) | `WaitingForConsentScreen` + `SmsBanner` |
| 6 | Utilisateur | Touche la bannière SMS (≡ clic sur le lien `sifip-ciba://consent?…`) | bannière `SmsBanner` |
| 7 | SIFIP | Ouvre l'écran de consentement (logo SIFIP + carte demande de la banque + liste des scopes) | `ConsentScreen` |
| 8 | Utilisateur | Touche **Autoriser** (ou **Refuser**) | boutons `AppPrimaryButton` / `AppSecondaryButton` |
| 9a | SIFIP | Active Fraud Engine et score la transaction (3 checks animés) | `ActivationScreen` |
| 9b | SIFIP | Si refus : retour direct à l'écran d'échec | `SuccessScreen` (variant `RejectedByUser`) |
| 10 | BMOI | Affiche le résultat final : virement confirmé / bloqué par fraude | `SuccessScreen` (variants `Approved` / `RejectedByFraud`) |

## 2. Bi-palette visuelle

L'app **change explicitement d'identité visuelle** entre les écrans de la
banque et l'écran SIFIP pour signaler à l'utilisateur qu'il interagit avec
deux entités distinctes (pattern OAuth 2.0 classique : on quitte l'app
relying-party pour l'auth server).

| Contexte | Couleur primaire | Composant top bar |
| :------- | :--------------- | :---------------- |
| Banque (Splash, Login, Dashboard, Transfer, Waiting, Activation, Success) | Violet BMOI `#5D2D8A` | `BankTopBar` |
| SIFIP (Consent) | Bleu SIFIP `#1F4FB5` + accent doré `#F2B033` | `SifipTopBar` |

## 3. Scénarios de démo

Le mock CIBA est piloté par l'enum `MockScenario`. Le scénario actif est
commutable depuis le coin haut-droit de l'écran **Transfer** (dropdown).

| Scénario              | Comportement                                                                          |
| :-------------------- | :------------------------------------------------------------------------------------ |
| `APPROVE_LOW_FRAUD`   | Consentement approuvé · score 12 % → virement OK                                      |
| `APPROVE_HIGH_FRAUD`  | Consentement approuvé · score 87 % → virement bloqué                                  |
| `USER_REJECTS`        | L'utilisateur refuse le consentement → virement annulé                                |
| `AMOUNT_BASED`        | Score selon le montant : ≤ 1 000 000 MGA → OK (12 %) ; > → bloqué (76 %)              |

### Override au build (CI)

```powershell
.\gradlew.bat assembleDebug -PcibaScenario=USER_REJECTS
```

Lu dans `BuildConfig.DEFAULT_CIBA_SCENARIO` au démarrage de l'app.

## 4. Architecture

```
app/src/main/java/com/sifip/cibademo/
├── CibaApplication.kt        ← composition root (CibaService + BankRepository)
├── MainActivity.kt           ← enableEdgeToEdge + AppTheme + AppNavGraph
├── ViewModelFactories.kt
├── navigation/
│   └── NavGraph.kt           ← Splash → Login → Dashboard → Transfer → Waiting
│                                 → Consent → Activation → Success
├── data/
│   ├── model/
│   │   ├── CibaModels.kt     ← CibaScope, ConsentRequest, ConsentDecision
│   │   ├── FraudResult.kt
│   │   └── AccountModels.kt
│   ├── mock/
│   │   ├── CibaService.kt    ← orchestrateur central (StateFlow partagé)
│   │   └── MockScenario.kt
│   └── repository/
│       └── BankRepository.kt
└── ui/
    ├── splash/SplashScreen.kt
    ├── login/{LoginScreen, LoginViewModel}
    ├── dashboard/{DashboardScreen, DashboardViewModel}
    ├── transfer/{TransferScreen, CibaFlowViewModel}
    ├── consent/{WaitingForConsentScreen, ConsentScreen}
    ├── activation/ActivationScreen.kt
    ├── success/SuccessScreen.kt
    ├── components/
    │   ├── TopBars.kt        ← BankTopBar (violet) + SifipTopBar (bleu)
    │   ├── AppButtons.kt     ← AppPrimaryButton + AppSecondaryButton
    │   ├── Logos.kt          ← BankLogo (PNG officiel) + SifipLogo (PNG officiel)
    │   ├── SmsBanner.kt      ← bannière SMS slide-down animée
    │   └── CheckStep.kt      ← step animé (Idle/Running/Ok/Failed)
    └── theme/{Color, Type, Theme}
```

### ViewModel partagé

`CibaFlowViewModel` est **scope Activity** (pas par destination NavGraph),
ce qui permet aux 5 écrans `Transfer → Waiting → Consent → Activation →
Success` de partager le même `StateFlow<CibaFlowUiState>` (formulaire,
requête CIBA, décision utilisateur, score fraude).

### Simulation du deeplink

Le `<intent-filter>` `sifip-ciba://consent` est déclaré dans le manifest
pour le réalisme, mais le déclencheur en démo est **interne** : la
`SmsBanner` (slide-down animée depuis le haut) joue le rôle de la
notification SMS, et son tap navigue directement vers `ConsentScreen`.
Dans une intégration réelle, ce serait l'OS Android qui résoudrait l'URL
en ouvrant cette même activité.

## 5. Compiler et lancer

```powershell
cd sifip-ciba-demo
gradle wrapper --gradle-version 8.9    # une seule fois
.\gradlew.bat installDebug
```

Pré-requis : Android Studio Hedgehog+, JDK 17, SDK 34.

Min SDK 26 (Android 8.0). Pas de réseau requis (mock complet).

## 6. Aller plus loin : connexion à la vraie API SIFIP

Pour brancher le vrai serveur OAuth2 / CIBA SIFIP, remplacer
`CibaService` par une implémentation Retrofit + Keycloak :

```kotlin
// avant (démo)
cibaService = CibaService(initial)

// après (prod)
cibaService = RealCibaClient(
    sifipAuthBase = "https://auth.34-53-128-84.sslip.io",
    bankClientId = BuildConfig.SIFIP_CLIENT_ID,
    bankPrivateKey = readPemFromAssets("private.pem"),
)
```

Endpoints OAuth2 CIBA documentés côté SIFIP :

```
POST /realms/sifip/protocol/openid-connect/ciba/auth        bc-authorize
POST /realms/sifip/protocol/openid-connect/token            poll auth_req_id
GET  /realms/sifip/protocol/openid-connect/ciba/consent     UI consent
```

## 7. Licence

Code propriétaire — usage **démo SIFIP uniquement**. Ne pas distribuer.
Les logos officiels BMOI et SIFIP (`bmoi_logo.png`, `sifip_logo.png`)
restent la propriété de leurs ayants droit respectifs.
