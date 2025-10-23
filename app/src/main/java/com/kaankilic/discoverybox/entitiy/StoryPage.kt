package com.kaankilic.discoverybox.entitiy

import android.graphics.Bitmap

data class StoryPage(
    val pageNumber: Int,
    val content: String,
    val imagePrompt: String,
    var imageBitmap: Bitmap? = null
)
