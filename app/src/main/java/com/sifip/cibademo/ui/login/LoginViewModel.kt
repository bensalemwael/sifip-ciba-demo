package com.sifip.cibademo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LoginPhase { Idle, Running, Success }

data class LoginUiState(
    val phoneNumber: String = "+261 32 12 345 67",
    val password: String = "",
    val phase: LoginPhase = LoginPhase.Idle,
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onPhoneChanged(v: String) = _state.update { it.copy(phoneNumber = v) }
    fun onPasswordChanged(v: String) = _state.update { it.copy(password = v) }

    fun login(onSuccess: () -> Unit) {
        if (_state.value.phase == LoginPhase.Running) return
        viewModelScope.launch {
            _state.update { it.copy(phase = LoginPhase.Running) }
            delay(800)
            _state.update { it.copy(phase = LoginPhase.Success) }
            onSuccess()
        }
    }
}
