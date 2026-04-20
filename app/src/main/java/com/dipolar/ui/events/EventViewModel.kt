package com.dipolar.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dipolar.data.model.*
import com.dipolar.data.repository.DummyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: DummyRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser

    private val _selectedInterestFilter = MutableStateFlow<Set<Interest>>(emptySet())
    val selectedInterestFilter: StateFlow<Set<Interest>> = _selectedInterestFilter.asStateFlow()

    private val _selectedLanguageFilter = MutableStateFlow<Language?>(null)
    val selectedLanguageFilter: StateFlow<Language?> = _selectedLanguageFilter.asStateFlow()

    // null = hepsi, true = sadece 1-on-1, false = sadece grup
    private val _meetingTypeFilter = MutableStateFlow<Boolean?>(null)
    val meetingTypeFilter: StateFlow<Boolean?> = _meetingTypeFilter.asStateFlow()

    val filteredEvents: StateFlow<List<Event>> = combine(
        repository.events,
        _selectedInterestFilter,
        _selectedLanguageFilter,
        _meetingTypeFilter
    ) { events, interestFilter, languageFilter, meetingType ->
        events.filter { event ->
            val typeMatch = when (meetingType) {
                true  -> event.isDateMeeting
                false -> !event.isDateMeeting
                null  -> true
            }
            val interestMatch = interestFilter.isEmpty() ||
                    event.interests.any { it in interestFilter }
            val languageMatch = languageFilter == null || event.language == languageFilter
            typeMatch && interestMatch && languageMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myEvents: StateFlow<List<Event>> = combine(
        repository.events,
        repository.currentUser
    ) { events, user ->
        events.filter { it.creatorId == user?.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingRequestEvents: StateFlow<List<Event>> = combine(
        repository.events,
        repository.currentUser
    ) { events, user ->
        if (user == null) emptyList()
        else events.filter { event ->
            event.creatorId == user.id &&
                    event.joinRequests.any { it.status == JoinRequestStatus.PENDING }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasActiveFilters: StateFlow<Boolean> = combine(
        _selectedInterestFilter, _selectedLanguageFilter, _meetingTypeFilter
    ) { i, l, m -> i.isNotEmpty() || l != null || m != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleInterestFilter(interest: Interest) {
        _selectedInterestFilter.update { if (interest in it) it - interest else it + interest }
    }

    fun setLanguageFilter(language: Language?) {
        _selectedLanguageFilter.value = language
    }

    fun setMeetingTypeFilter(isDate: Boolean?) {
        _meetingTypeFilter.value = isDate
    }

    fun clearFilters() {
        _selectedInterestFilter.value = emptySet()
        _selectedLanguageFilter.value = null
        _meetingTypeFilter.value = null
    }

    fun createEvent(
        title: String, description: String, date: String, location: String,
        maxParticipants: Int, interests: List<Interest>, language: Language,
        isDateMeeting: Boolean = false
    ) {
        val user = repository.currentUser.value ?: return
        viewModelScope.launch {
            repository.createEvent(
                title = title, description = description, date = date, location = location,
                maxParticipants = maxParticipants, interests = interests, language = language,
                isDateMeeting = isDateMeeting, creatorId = user.id,
                creatorName = user.name, creatorAvatarUrl = user.avatarUrl
            )
        }
    }

    fun sendJoinRequest(eventId: String) {
        val user = repository.currentUser.value ?: return
        viewModelScope.launch { repository.sendJoinRequest(eventId, user) }
    }

    fun respondToJoinRequest(eventId: String, requestId: String, accept: Boolean) {
        viewModelScope.launch { repository.respondToJoinRequest(eventId, requestId, accept) }
    }

    fun getJoinStatus(eventId: String): JoinRequestStatus? {
        val userId = repository.currentUser.value?.id ?: return null
        return repository.getJoinRequestStatus(eventId, userId)
    }

    fun getEventById(eventId: String): Event? = repository.events.value.find { it.id == eventId }
}
