package com.kaankilic.discoverybox.entitiy

data class Word(
    val nameEn: String,
    val nameTr: String,
    val imageUrl: String,
    var isVisible: Boolean = true // Varsayılan olarak görünür
)

