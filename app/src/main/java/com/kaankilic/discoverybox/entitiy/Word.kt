package com.kaankilic.discoverybox.entitiy

data class Word(
    val nameTr: String,
    val nameEn: String,
    val imageUrl: String,
    var isVisible: Boolean = true // Varsayılan olarak görünür
)

