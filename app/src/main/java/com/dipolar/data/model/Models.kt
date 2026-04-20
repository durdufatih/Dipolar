package com.dipolar.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

enum class Interest(val label: String, val emoji: String) {
    MUSIC("Müzik", "🎵"),
    SPORTS("Spor", "⚽"),
    TECHNOLOGY("Teknoloji", "💻"),
    ART("Sanat", "🎨"),
    TRAVEL("Seyahat", "✈️"),
    FOOD("Yemek", "🍕"),
    BOOKS("Kitap", "📚"),
    CINEMA("Sinema", "🎬"),
    GAMING("Oyun", "🎮"),
    PHOTOGRAPHY("Fotoğrafçılık", "📷"),
    NATURE("Doğa", "🌿"),
    FITNESS("Fitness", "💪")
}

enum class Language(val label: String, val flag: String) {
    TURKISH("Türkçe", "🇹🇷"),
    ENGLISH("English", "🇬🇧"),
    GERMAN("Deutsch", "🇩🇪"),
    FRENCH("Français", "🇫🇷"),
    SPANISH("Español", "🇪🇸"),
    ITALIAN("Italiano", "🇮🇹"),
    JAPANESE("日本語", "🇯🇵"),
    ARABIC("العربية", "🇸🇦")
}

@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int,
    val bio: String,
    val avatarUrl: String,
    val interests: List<Interest>,
    val languages: List<Language>
) : Parcelable

enum class JoinRequestStatus {
    PENDING, ACCEPTED, REJECTED
}

@Parcelize
data class JoinRequest(
    val id: String,
    val eventId: String,
    val requesterId: String,
    val requesterName: String,
    val requesterAvatarUrl: String,
    val status: JoinRequestStatus
) : Parcelable

@Parcelize
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatarUrl: String,
    val date: String,
    val location: String,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val interests: List<Interest>,
    val language: Language,
    val joinRequests: List<JoinRequest> = emptyList()
) : Parcelable {
    val isFull: Boolean get() = currentParticipants >= maxParticipants
    val spotsLeft: Int get() = maxParticipants - currentParticipants
}
