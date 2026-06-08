package com.sifip.cibademo.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sifip.cibademo.CibaApplication
import com.sifip.cibademo.CibaFlowViewModelFactory
import com.sifip.cibademo.DashboardViewModelFactory
import com.sifip.cibademo.LoginViewModelFactory
import com.sifip.cibademo.R
import com.sifip.cibademo.ui.activation.ActivationScreen
import com.sifip.cibademo.ui.consent.ConsentScreen
import com.sifip.cibademo.ui.consent.WaitingForConsentScreen
import com.sifip.cibademo.ui.dashboard.DashboardScreen
import com.sifip.cibademo.ui.dashboard.DashboardViewModel
import com.sifip.cibademo.ui.login.LoginScreen
import com.sifip.cibademo.ui.login.LoginViewModel
import com.sifip.cibademo.ui.splash.SplashScreen
import com.sifip.cibademo.ui.success.SuccessScreen
import com.sifip.cibademo.ui.transfer.CibaFlowViewModel
import com.sifip.cibademo.ui.transfer.TransferScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val TRANSFER = "transfer"
    const val WAITING = "waiting"
    const val CONSENT = "consent"
    const val ACTIVATION = "activation"
    const val SUCCESS = "success"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as CibaApplication
    val activity = context as ComponentActivity

    // ViewModel partagé sur tout le flux CIBA, scope Activity.
    val cibaFactory = CibaFlowViewModelFactory(
        app = app,
        bankName = "BMOI Madagascar",
        bankLogoResId = R.drawable.bmoi_logo,
    )

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onTimeout = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.LOGIN) {
            val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(app))
            LoginScreen(
                viewModel = vm,
                onAuthenticated = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.DASHBOARD) {
            val vm: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(app))
            DashboardScreen(
                viewModel = vm,
                onTransferClicked = { navController.navigate(Routes.TRANSFER) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.TRANSFER) {
            val vm: CibaFlowViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = cibaFactory,
            )
            TransferScreen(
                viewModel = vm,
                onBack = {
                    navController.popBackStack(Routes.DASHBOARD, inclusive = false)
                },
                onSubmit = { navController.navigate(Routes.WAITING) },
            )
        }

        composable(Routes.WAITING) {
            val vm: CibaFlowViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = cibaFactory,
            )
            WaitingForConsentScreen(
                viewModel = vm,
                onSmsTapped = { navController.navigate(Routes.CONSENT) },
                onBack = {
                    navController.popBackStack(Routes.DASHBOARD, inclusive = false)
                },
            )
        }

        composable(Routes.CONSENT) {
            val vm: CibaFlowViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = cibaFactory,
            )
            ConsentScreen(
                viewModel = vm,
                onApproved = {
                    navController.navigate(Routes.ACTIVATION) {
                        popUpTo(Routes.WAITING) { inclusive = true }
                    }
                },
                onRejected = {
                    navController.navigate(Routes.SUCCESS) {
                        popUpTo(Routes.WAITING) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.ACTIVATION) {
            val vm: CibaFlowViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = cibaFactory,
            )
            ActivationScreen(
                viewModel = vm,
                onDone = {
                    navController.navigate(Routes.SUCCESS) {
                        popUpTo(Routes.ACTIVATION) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack(Routes.DASHBOARD, inclusive = false)
                },
            )
        }

        composable(Routes.SUCCESS) {
            val vm: CibaFlowViewModel = viewModel(
                viewModelStoreOwner = activity,
                factory = cibaFactory,
            )
            SuccessScreen(
                viewModel = vm,
                onBackToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }
    }
}
