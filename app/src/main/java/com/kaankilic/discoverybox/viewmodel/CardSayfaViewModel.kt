package com.kaankilic.discoverybox.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.entitiy.Word
import kotlinx.coroutines.launch

class CardSayfaViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    val words = mutableStateListOf<Word>()

    // Karıştırılmış kelimeler ve görseller
    val shuffledWords = mutableStateListOf<Word>()
    val shuffledImages = mutableStateListOf<Word>()

    init {
        loadWords()
    }

    fun loadWords() = viewModelScope.launch {
        firestore.collection("meyveler")
            .get()
            .addOnSuccessListener { documents ->
                words.clear()
                for (document in documents) {
                    val nameTr = document.getString("isim_tr") ?: ""
                    val nameEn = document.getString("isim_en") ?: ""
                    val imageUrl = document.getString("gorsel_url") ?: ""
                    words.add(Word(nameTr, nameEn, imageUrl))
                }
                // İlk başta karıştırma işlemi
                shuffledWords.clear()
                shuffledImages.clear()
                shuffledWords.addAll(words.shuffled()) // Kelimeleri karıştır
                shuffledImages.addAll(words.shuffled()) // Görselleri karıştır
            }
            .addOnFailureListener { e ->
                println("Veri çekilemedi: $e")
            }
    }

    fun removeWord(word: Word) {
        words.remove(word) // Kelimeyi listeden kaldır
        shuffledWords.remove(word) // Karıştırılmış kelime listesinden kaldır
        shuffledImages.remove(word) // Karıştırılmış görsel listesinden kaldır
    }
}

