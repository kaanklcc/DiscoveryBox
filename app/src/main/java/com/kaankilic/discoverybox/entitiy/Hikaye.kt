package com.kaankilic.discoverybox.entitiy

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Hikaye(
    val id: String = "",
    val title: String = "",
    @PropertyName("hikaye") val content: String = "",
    val imageUrl: String = "",
    val timestamp: Timestamp? = null
)


