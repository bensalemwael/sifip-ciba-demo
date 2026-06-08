package com.sifip.cibademo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sifip.cibademo.ui.dashboard.DashboardViewModel
import com.sifip.cibademo.ui.login.LoginViewModel
import com.sifip.cibademo.ui.transfer.CibaFlowViewModel

class LoginViewModelFactory(private val app: CibaApplication) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        LoginViewModel(app.sifipAuth) as T
}

class DashboardViewModelFactory(private val app: CibaApplication) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DashboardViewModel(app.bankRepository) as T
}

class CibaFlowViewModelFactory(
    private val app: CibaApplication,
    private val bankName: String,
    private val bankLogoResId: Int,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CibaFlowViewModel(
            cibaService = app.cibaService,
            bankName = bankName,
            bankLogoResId = bankLogoResId,
        ) as T
}
