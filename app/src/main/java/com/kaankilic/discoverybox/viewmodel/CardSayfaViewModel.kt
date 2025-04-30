package com.kaankilic.discoverybox.viewmodel


import androidx.compose.runtime.MutableState
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

    fun loadWords() =
        viewModelScope.launch {
        firestore.collection("kelimeler")
            .get()
            .addOnSuccessListener { documents ->
                words.clear()
                for (document in documents) {
                    val colour = document.getString("colour") ?: ""
                    val nameTr = document.getString("isim_en") ?: ""
                    val nameEn = document.getString("isim_tr") ?: ""
                    val imageUrl = document.getString("gorsel_url") ?: ""
                    words.add(Word(colour,nameTr, nameEn, imageUrl))
                }
                prepareShuffledData()
            }
            .addOnFailureListener { e ->
                println("Veri çekilemedi: $e")
            }
    }

    private fun prepareShuffledData() {
        val correctWord = words.random()
        val incorrectWords = words.filter { it != correctWord }.shuffled().take(2)
        val shuffledWordsList = listOf(correctWord) + incorrectWords
        shuffledWords.clear()
        shuffledWords.addAll(shuffledWordsList.shuffled())

        val shuffledImagesList = listOf(correctWord) + incorrectWords.map { word ->
            words.first { it.imageUrl == word.imageUrl }
        }
        shuffledImages.clear()
        shuffledImages.addAll(shuffledImagesList.shuffled())
    }

    fun removeWord(word: Word) {
        shuffledWords.remove(word)
        shuffledImages.removeIf { it.imageUrl == word.imageUrl }
    }

    fun loadNextGroup(currentGroupIndex: Int, groupSize: Int,isGameOver : MutableState<Boolean>) {
        val startIndex = currentGroupIndex* groupSize
        val nextGroupWords = words.drop(startIndex).take(groupSize / 3)
        val nextGroupImages = words.drop(startIndex).take(groupSize)

        if (nextGroupWords.isNotEmpty() && nextGroupImages.isNotEmpty()) {
            shuffledWords.clear()
            shuffledWords.addAll(nextGroupWords.shuffled())

            shuffledImages.clear()
            shuffledImages.addAll(nextGroupImages.shuffled())

            currentGroupIndex+1
            if (currentGroupIndex>=2){
            isGameOver.value=true
            }
            prepareShuffledData()

        } else {
            println("Tüm gruplar tamamlandı veya yetersiz veri!")

        }
    }

}






