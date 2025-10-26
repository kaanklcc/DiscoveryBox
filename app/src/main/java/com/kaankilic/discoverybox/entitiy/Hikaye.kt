package com.kaankilic.discoverybox.entitiy

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Hikaye(
    val id: String = "",
    val title: String = "",
    @PropertyName("hikaye") val content: String = "",
    val imageUrl: String = "", // Eski hikayeler için
    val imageUrls: List<String> = emptyList(), // Yeni: her sayfa için ayrı görsel
    val timestamp: Timestamp? = null
)


