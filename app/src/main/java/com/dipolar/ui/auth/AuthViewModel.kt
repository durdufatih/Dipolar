package com.dipolar.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dipolar.data.model.User
import com.dipolar.data.repository.DummyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: DummyRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: StateFlow<User?> = repository.currentUser

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("E-posta ve şifre boş olamaz")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.login(email, password)
            _authState.value = if (user != null) AuthState.Success(user)
            else AuthState.Error("E-posta veya şifre hatalı")
        }
    }

    fun signup(name: String, email: String, password: String, ageStr: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Tüm alanları doldurun")
            return
        }
        val age = ageStr.toIntOrNull() ?: 0
        if (age < 18) {
            _authState.value = AuthState.Error("18 yaşından büyük olmalısınız")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.signup(name, email, password, age)
            _authState.value = AuthState.Success(user)
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
