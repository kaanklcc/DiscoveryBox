package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.entitiy.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor (val firestore: FirebaseFirestore) : ViewModel() {
    //private val firestore = FirebaseFirestore.getInstance()

    private val _currentData = MutableStateFlow<Pair<String, List<Word>>?>(null)
    val currentData: StateFlow<Pair<String, List<Word>>?> = _currentData

    fun loadNewData() {
        viewModelScope.launch {
            firestore.collection("kelimeler")
                .get()
                .addOnSuccessListener { result ->
                    val words = result.documents.map { doc ->
                        Word(

                            colour = doc.getString("colour") ?: "",
                            nameEn = doc.getString("isim_en") ?: "",
                            nameTr = doc.getString("isim_tr") ?: "",
                            imageUrl = doc.getString("gorsel_url") ?: "",
                        )
                    }

                    if (words.isNotEmpty()) {
                        val correctWord = words.random() // Doğru görsel
                        val wrongWords = words.filter { it.colour != correctWord.colour }.shuffled().take(2) // Yanlış görseller
                        val mixedWords = (wrongWords + correctWord).shuffled() // Tüm görselleri karıştır

                        _currentData.value = correctWord.colour to mixedWords
                    }
                }
        }
    }
}

