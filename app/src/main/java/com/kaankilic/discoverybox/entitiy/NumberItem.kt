package com.kaankilic.discoverybox.entitiy

data class NumberItem(
    val gorsel_Url: String = "",
    val isim_en: String = "",
    val isim_tr: String = "",
    val numara: String = "",
    var isMatched: Boolean = false // Eşleşme durumu
)
