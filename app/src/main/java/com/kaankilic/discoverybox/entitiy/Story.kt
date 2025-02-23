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

fun getAllGames(): List<Story>{
    return listOf<Story>(
        Story("wordGame", R.drawable.words,"wordGame"),
        Story("matchingGame", R.drawable.matching,"matchingGame"),
        Story("colorGame", R.drawable.colour,"colorGame"),
        Story("rakam", R.drawable.numbers,"guncelHayat"),
        Story("duyuOrgan", R.drawable.sense,"diger"),
    )
}
