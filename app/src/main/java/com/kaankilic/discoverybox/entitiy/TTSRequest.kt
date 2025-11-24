package com.kaankilic.discoverybox.entitiy

data class TTSRequest(
    val model: String = "gpt-4o-mini-tts",
    val input: String,
    val voice: String = "coral",
    val response_format: String = "mp3",
    val instructions: String = """
      Voice Affect: Gentle, nurturing, and caring; like a loving storyteller reading to a child before bedtime.

    Tone: Warm, calm, and comforting; evoke a sense of safety and imagination.

    Pacing: Slow and steady; allow time between sentences for the child to absorb the story and visualize the scenes.

    Emotion: Softly expressive; reflect wonder, curiosity, and kindness in every phrase.

    Pronunciation: Clear and smooth articulation, with a light melodic rhythm to engage young listeners.

    Pronunciation: Clear and smooth articulation, with a light melodic rhythm to engage young listeners.
""".trimIndent()
)

data class ImageRequest(
    val model: String = "dall-e-3",
    val prompt: String,
    val size: String = "1024x1024",
    val quality: String = "standard", // veya "hd" (isteğe bağlı),
    val n: Int = 1
)

data class ImageResponse(
    val created: Long,
    val data: List<ImageData>
)

data class ImageData(
    val url: String
)

