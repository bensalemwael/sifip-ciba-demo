package com.sifip.cibademo.ui.dashboard

import androidx.lifecycle.ViewModel
import com.sifip.cibademo.data.model.BankAccount
import com.sifip.cibademo.data.repository.BankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(val account: BankAccount)

class DashboardViewModel(repository: BankRepository) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState(repository.loadAccount()))
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()
}
