package com.kaankilic.discoverybox.entitiy

import com.kaankilic.discoverybox.R

data class Story(
    var title : String,
    var imageRes: Int,
    val category: String
)
/*fun getAllGames(): List<Story>{
    return listOf<Story>(
        Story("Words", R.drawable.words,"wordGame"),
        Story("Matching", R.drawable.matching,"matchingGame"),
        Story("Colors", R.drawable.colour,"colorGame"),
        Story("Numbers", R.drawable.numbers,"numberGame"),
       // Story("duyuOrgan", R.drawable.sense,"diger"),
    )
}*/
