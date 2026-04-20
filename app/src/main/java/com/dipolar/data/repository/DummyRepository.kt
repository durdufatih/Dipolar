package com.dipolar.data.repository

import com.dipolar.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyRepository @Inject constructor() {

    private val users = mutableListOf(
        User(
            id = "u1",
            name = "Ayşe Kara",
            email = "ayse@example.com",
            age = 25,
            bio = "Müzik ve seyahat tutkunu. Yeni insanlarla tanışmayı seviyorum!",
            avatarUrl = "https://i.pravatar.cc/150?img=1",
            interests = listOf(Interest.MUSIC, Interest.TRAVEL, Interest.FOOD),
            languages = listOf(Language.TURKISH, Language.ENGLISH)
        ),
        User(
            id = "u2",
            name = "Mehmet Yılmaz",
            email = "mehmet@example.com",
            age = 28,
            bio = "Yazılım geliştirici, oyun ve teknoloji meraklısı.",
            avatarUrl = "https://i.pravatar.cc/150?img=12",
            interests = listOf(Interest.TECHNOLOGY, Interest.GAMING, Interest.SPORTS),
            languages = listOf(Language.TURKISH, Language.ENGLISH, Language.GERMAN)
        ),
        User(
            id = "u3",
            name = "Zeynep Demir",
            email = "zeynep@example.com",
            age = 23,
            bio = "Fotoğrafçılık ve doğa yürüyüşleri yapıyorum.",
            avatarUrl = "https://i.pravatar.cc/150?img=5",
            interests = listOf(Interest.PHOTOGRAPHY, Interest.NATURE, Interest.ART),
            languages = listOf(Language.TURKISH, Language.FRENCH)
        ),
        User(
            id = "u4",
            name = "Can Arslan",
            email = "can@example.com",
            age = 30,
            bio = "Sinema ve kitap kurdu. Felsefi sohbetleri seviyorum.",
            avatarUrl = "https://i.pravatar.cc/150?img=15",
            interests = listOf(Interest.CINEMA, Interest.BOOKS, Interest.ART),
            languages = listOf(Language.TURKISH, Language.ENGLISH, Language.SPANISH)
        ),
        User(
            id = "u5",
            name = "Elif Şahin",
            email = "elif@example.com",
            age = 26,
            bio = "Fitness ve sağlıklı yaşam konusunda tutkulum.",
            avatarUrl = "https://i.pravatar.cc/150?img=9",
            interests = listOf(Interest.FITNESS, Interest.SPORTS, Interest.FOOD),
            languages = listOf(Language.TURKISH, Language.ENGLISH)
        )
    )

    private val _events = MutableStateFlow(
        mutableListOf(
            Event(
                id = "e1",
                title = "Boğaz'da Müzik Sohbeti",
                description = "Boğaz kenarında oturup müzik hakkında konuşalım. Favori sanatçılarınızı paylaşın!",
                creatorId = "u1",
                creatorName = "Ayşe Kara",
                creatorAvatarUrl = "https://i.pravatar.cc/150?img=1",
                date = "2026-05-10",
                location = "Bebek, İstanbul",
                maxParticipants = 4,
                currentParticipants = 1,
                interests = listOf(Interest.MUSIC, Interest.TRAVEL),
                language = Language.TURKISH,
                joinRequests = emptyList()
            ),
            Event(
                id = "e2",
                title = "Tech & Coffee Buluşması",
                description = "En yeni teknoloji trendlerini konuşacağız. Yapay zeka, blockchain ve daha fazlası!",
                creatorId = "u2",
                creatorName = "Mehmet Yılmaz",
                creatorAvatarUrl = "https://i.pravatar.cc/150?img=12",
                date = "2026-05-12",
                location = "Kadıköy, İstanbul",
                maxParticipants = 3,
                currentParticipants = 2,
                interests = listOf(Interest.TECHNOLOGY, Interest.GAMING),
                language = Language.ENGLISH,
                joinRequests = emptyList()
            ),
            Event(
                id = "e3",
                title = "Belgrad Ormanı Fotoğraf Gezisi",
                description = "Doğanın içinde fotoğraf çekmeyi sevenlere. Tüm seviyelere açık!",
                creatorId = "u3",
                creatorName = "Zeynep Demir",
                creatorAvatarUrl = "https://i.pravatar.cc/150?img=5",
                date = "2026-05-15",
                location = "Belgrad Ormanı, İstanbul",
                maxParticipants = 5,
                currentParticipants = 1,
                interests = listOf(Interest.PHOTOGRAPHY, Interest.NATURE),
                language = Language.TURKISH,
                joinRequests = emptyList()
            ),
            Event(
                id = "e4",
                title = "Film Kulübü: Sinema Klasikleri",
                description = "Her hafta bir klasik film izleyip tartışıyoruz. Bu hafta: 2001 A Space Odyssey",
                creatorId = "u4",
                creatorName = "Can Arslan",
                creatorAvatarUrl = "https://i.pravatar.cc/150?img=15",
                date = "2026-05-17",
                location = "Cihangir, İstanbul",
                maxParticipants = 6,
                currentParticipants = 3,
                interests = listOf(Interest.CINEMA, Interest.ART),
                language = Language.TURKISH,
                joinRequests = emptyList()
            ),
            Event(
                id = "e5",
                title = "Sabah Koşusu & Kahvaltı",
                description = "Erken kalkanlar için! Koşu sonrası güzel bir kahvaltı yapacağız.",
                creatorId = "u5",
                creatorName = "Elif Şahin",
                creatorAvatarUrl = "https://i.pravatar.cc/150?img=9",
                date = "2026-05-20",
                location = "Maçka Parkı, İstanbul",
                maxParticipants = 8,
                currentParticipants = 2,
                interests = listOf(Interest.FITNESS, Interest.SPORTS, Interest.FOOD),
                language = Language.TURKISH,
                joinRequests = emptyList()
            ),
            Event(
                id = "e6",
                title = "Kitap Kulübü Buluşması",
                description = "Bu ay Orhan Pamuk'un 'Kar' romanını okuyoruz. Görüşlerinizi paylaşın!",
                creatorId = "u4",
                creatorName = "Can Arslan",
                creatorAvatarUrl = "https://i.pravatar.cc/150?img=15",
                date = "2026-05-22",
                location = "Moda, İstanbul",
                maxParticipants = 4,
                currentParticipants = 4,
                interests = listOf(Interest.BOOKS, Interest.ART),
                language = Language.TURKISH,
                joinRequests = emptyList()
            )
        )
    )

    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private var _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(email: String, password: String): User? {
        val user = users.find { it.email == email }
        if (user != null) _currentUser.value = user
        return user
    }

    fun signup(name: String, email: String, password: String, age: Int): User {
        val newUser = User(
            id = "u${users.size + 1}",
            name = name,
            email = email,
            age = age,
            bio = "",
            avatarUrl = "https://i.pravatar.cc/150?img=${(1..70).random()}",
            interests = emptyList(),
            languages = listOf(Language.TURKISH)
        )
        users.add(newUser)
        _currentUser.value = newUser
        return newUser
    }

    fun updateUserProfile(userId: String, bio: String, interests: List<Interest>, languages: List<Language>) {
        val index = users.indexOfFirst { it.id == userId }
        if (index != -1) {
            users[index] = users[index].copy(bio = bio, interests = interests, languages = languages)
            if (_currentUser.value?.id == userId) {
                _currentUser.value = users[index]
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun createEvent(
        title: String,
        description: String,
        date: String,
        location: String,
        maxParticipants: Int,
        interests: List<Interest>,
        language: Language,
        creatorId: String,
        creatorName: String,
        creatorAvatarUrl: String
    ) {
        val newEvent = Event(
            id = "e${_events.value.size + 1}",
            title = title,
            description = description,
            creatorId = creatorId,
            creatorName = creatorName,
            creatorAvatarUrl = creatorAvatarUrl,
            date = date,
            location = location,
            maxParticipants = maxParticipants,
            currentParticipants = 1,
            interests = interests,
            language = language,
            joinRequests = emptyList()
        )
        _events.update { list -> (list + newEvent).toMutableList() }
    }

    fun sendJoinRequest(eventId: String, user: User) {
        _events.update { list ->
            list.map { event ->
                if (event.id == eventId) {
                    val alreadyRequested = event.joinRequests.any { it.requesterId == user.id }
                    if (alreadyRequested) return@map event
                    val newRequest = JoinRequest(
                        id = "jr${System.currentTimeMillis()}",
                        eventId = eventId,
                        requesterId = user.id,
                        requesterName = user.name,
                        requesterAvatarUrl = user.avatarUrl,
                        status = JoinRequestStatus.PENDING
                    )
                    event.copy(joinRequests = event.joinRequests + newRequest)
                } else event
            }.toMutableList()
        }
    }

    fun respondToJoinRequest(eventId: String, requestId: String, accept: Boolean) {
        _events.update { list ->
            list.map { event ->
                if (event.id == eventId) {
                    val updatedRequests = event.joinRequests.map { req ->
                        if (req.id == requestId) {
                            req.copy(status = if (accept) JoinRequestStatus.ACCEPTED else JoinRequestStatus.REJECTED)
                        } else req
                    }
                    val newCount = if (accept) event.currentParticipants + 1 else event.currentParticipants
                    event.copy(joinRequests = updatedRequests, currentParticipants = newCount)
                } else event
            }.toMutableList()
        }
    }

    fun getJoinRequestStatus(eventId: String, userId: String): JoinRequestStatus? {
        return _events.value
            .find { it.id == eventId }
            ?.joinRequests
            ?.find { it.requesterId == userId }
            ?.status
    }

    fun getEventsForUser(userId: String): List<Event> {
        return _events.value.filter { it.creatorId == userId }
    }
}
