package com.kaankilic.discoverybox.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.entitiy.NumberItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NumberGameViewModel : ViewModel() {

    private val _items = mutableStateListOf<NumberItem>()
    val items: List<NumberItem> get() = _items

    private val hiddenNumbers = mutableStateListOf<String>()
    private val droppedCorrectly = mutableStateListOf<String>()

    private val _imageItems = mutableStateListOf<NumberItem>()
    val imageItems: List<NumberItem> get() = _imageItems

    private val _numberItems = mutableStateListOf<String>()
    val numberItems: List<String> get() = _numberItems

    init {
        fetchDataFromFirebase()
    }


    private fun fetchDataFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("rakamlar").get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    NumberItem(
                        gorsel_Url = doc.getString("gorsel_url") ?: "",
                        isim_en = doc.getString("isim_en") ?: "",
                        isim_tr = doc.getString("isim_tr") ?: "",
                        numara = doc.getString("numara") ?: ""
                    )
                }

                // Aynı numaraya ait görselleri grupla
                val groupedByNumber = list.groupBy { it.numara }

                // Her numara için bir görsel rastgele seç
                val onePerNumber = groupedByNumber.mapValues { (_, items) ->
                    items.random()
                }.values.toList()

                // Rastgele 3 farklı numara seç
                val selectedItems = onePerNumber.shuffled().take(3)

                // Rakamlar ve görselleri eşleştir
                val numberImagePairs = selectedItems.map {
                    Pair(it.numara, it.gorsel_Url)
                }

                // Karıştır
                val shuffledPairs = numberImagePairs.shuffled()

                // Karıştırılmış eşleşmeleri NumberItem listesine dönüştür
                val mixedList = shuffledPairs.map { (numara, gorsel) ->
                    NumberItem(
                        gorsel_Url = gorsel,
                        isim_en = "",
                        isim_tr = "",
                        numara = numara
                    )
                }
                // Listeyi güncelle
                _items.clear()
                _items.addAll(mixedList)


                _numberItems.clear()
                _numberItems.addAll(mixedList.map { it.numara }.shuffled()) // sadece numaraları al ve karıştır

            }
    }





    fun removeItem(number: String) {
        if (!hiddenNumbers.contains(number)) {
            hiddenNumbers.add(number)
            Log.d("DragDrop", "Rakam gizlendi: $number")

            // Eğer 3 eşleşme tamamlandıysa yeni veri getir
            if (hiddenNumbers.size == 3) {
                Log.d("DragDrop", "🎉 Tüm rakamlar eşleşti! Yeni veri getiriliyor...")
                resetGame()
            }
        }
    }
    private fun resetGame() {
        // Listeleri temizle
        hiddenNumbers.clear()
        droppedCorrectly.clear()
        _items.clear()
        _numberItems.clear() // <-- Bu satırı mutlaka ekle!
        // Yeni veriyi çek
        fetchDataFromFirebase()
    }

    fun isNumberVisible(number: String): Boolean {
        return !hiddenNumbers.contains(number)
    }

    fun setDroppedCorrectly(number: String) {
        if (!droppedCorrectly.contains(number)) {
            droppedCorrectly.add(number)
            Log.d("DragDrop", "🔹 setDroppedCorrectly çağrıldı: $number")

            viewModelScope.launch {
                delay(200) // 200ms bekleyerek birden fazla eşleşmeyi önle
                removeItem(number)
            }
        }
    }

    fun getCorrectMatch(number: String): String? {
        return _items.find { it.numara == number }?.numara
    }


}


