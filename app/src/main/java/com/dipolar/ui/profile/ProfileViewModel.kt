package com.dipolar.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dipolar.data.model.Interest
import com.dipolar.data.model.Language
import com.dipolar.data.model.User
import com.dipolar.data.repository.DummyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: DummyRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser

    fun updateProfile(bio: String, interests: List<Interest>, languages: List<Language>) {
        viewModelScope.launch {
            val userId = repository.currentUser.value?.id ?: return@launch
            repository.updateUserProfile(userId, bio, interests, languages)
        }
    }

    fun logout() {
        repository.logout()
    }
}
