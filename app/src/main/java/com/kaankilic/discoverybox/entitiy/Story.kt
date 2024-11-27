package com.kaankilic.discoverybox.entitiy

import com.kaankilic.discoverybox.R

data class Story(
    var title : String,
    var imageRes: Int,
    val category: String
)

fun getAllStory(): List<Story>{
   return listOf<Story>(
      Story("hikaye", R.drawable.story,"hikaye"),
       Story("dil", R.drawable.lang,"dil"),
       Story("bilim", R.drawable.science,"bilim"),
       Story("guncelHayat", R.drawable.dailylife,"guncelHayat"),
       Story("diger", R.drawable.other,"diger"),
       Story("saveSayfa", R.drawable.save,"saveSayfa")





   )
}
