package com.kaankilic.discoverybox.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.entitiy.NumberItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NumberGameViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    val numberItems = mutableStateOf<List<NumberItem>>(emptyList())
    val currentQuestion = mutableStateOf<NumberItem?>(null)

    init {
        fetchNumbers()
    }

    private fun fetchNumbers() {
        viewModelScope.launch {
            firestore.collection("rakamlar")
                .get()
                .addOnSuccessListener { result ->
                    val numbers = result.documents.mapNotNull { document ->
                        val isimTr = document.getString("isim_tr") ?: ""
                        val isimEn = document.getString("isim_en") ?: ""
                        val numara = document.getString("numara") ?: ""
                        val gorselUrl = document.getString("gorsel_url") ?: ""
                        NumberItem(gorselUrl, isimEn, isimTr, numara)
                    }
                    numberItems.value = numbers
                    loadNextQuestion()
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }

    fun loadNextQuestion() {
        if (numberItems.value.isNotEmpty()) {
            currentQuestion.value = numberItems.value.random()
        }
    }

    fun checkAnswer(selectedNumber: String): Boolean {
        return currentQuestion.value?.numara == selectedNumber
    }

    fun markItemAsMatched(item: NumberItem) {
        // Görsel ve rakamı eşleşmiş olarak işaretle ve listelerden çıkar
        numberItems.value = numberItems.value.filterNot { it.numara == item.numara }
    }
}